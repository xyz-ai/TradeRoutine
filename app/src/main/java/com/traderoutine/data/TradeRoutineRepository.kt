package com.traderoutine.data

import androidx.room.withTransaction
import com.traderoutine.data.local.AppDatabase
import com.traderoutine.data.local.DailyTaskRecordEntity
import com.traderoutine.data.local.TaskTemplateEntity
import com.traderoutine.model.AppSettings
import com.traderoutine.model.DailyStatus
import com.traderoutine.model.DailyTaskItem
import com.traderoutine.model.TaskTemplate
import com.traderoutine.model.TaskTemplateInput
import com.traderoutine.model.UiLanguage
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class TradeRoutineRepository(
    private val database: AppDatabase,
    private val settingsStore: AppSettingsStore,
) {
    private val taskTemplateDao = database.taskTemplateDao()
    private val dailyTaskRecordDao = database.dailyTaskRecordDao()
    private val currentDate = MutableStateFlow(LocalDate.now())

    val currentDateFlow: StateFlow<LocalDate> = currentDate.asStateFlow()
    val settingsFlow: Flow<AppSettings> = settingsStore.settingsFlow

    fun observeCurrentDayTasks(): Flow<Pair<LocalDate, List<DailyTaskItem>>> {
        return currentDateFlow.flatMapLatest { date ->
            observeTasksForDate(date).map { tasks -> date to tasks }
        }
    }

    fun observeTasksForDate(date: LocalDate): Flow<List<DailyTaskItem>> {
        return dailyTaskRecordDao.observeByDate(date.toStorageKey()).map { records ->
            records.map { record -> record.toModel() }
        }
    }

    fun observeMonthStatuses(month: YearMonth): Flow<Map<LocalDate, DailyStatus>> {
        val start = month.atDay(1).toStorageKey()
        val end = month.atEndOfMonth().toStorageKey()
        return dailyTaskRecordDao.observeSummariesBetween(start, end).map { summaries ->
            summaries.associate { summary ->
                LocalDate.parse(summary.date) to DailyStatus.fromCounts(
                    totalCount = summary.totalCount,
                    completedCount = summary.completedCount
                )
            }
        }
    }

    suspend fun refreshCurrentDate() {
        val today = LocalDate.now()
        currentDate.value = today
        syncTodayRecords(today)
    }

    suspend fun getTemplate(templateId: Long): TaskTemplate? {
        return taskTemplateDao.getById(templateId)?.toModel()
    }

    suspend fun addTemplate(input: TaskTemplateInput) {
        database.withTransaction {
            val nextOrder = (taskTemplateDao.getMaxSortOrder() ?: -1) + 1
            val templateId = taskTemplateDao.insert(
                TaskTemplateEntity(
                    title = input.title.trim(),
                    note = input.note.trim(),
                    durationText = input.durationText.trim(),
                    startTime = input.startTime.trim(),
                    endTime = input.endTime.trim(),
                    sortOrder = nextOrder
                )
            )
            dailyTaskRecordDao.upsert(
                DailyTaskRecordEntity(
                    date = currentDate.value.toStorageKey(),
                    templateId = templateId,
                    titleSnapshot = input.title.trim(),
                    noteSnapshot = input.note.trim(),
                    durationTextSnapshot = input.durationText.trim(),
                    startTimeSnapshot = input.startTime.trim(),
                    endTimeSnapshot = input.endTime.trim(),
                    isCompleted = false,
                    orderIndex = nextOrder
                )
            )
        }
    }

    suspend fun updateTemplate(templateId: Long, input: TaskTemplateInput) {
        database.withTransaction {
            val currentTemplate = taskTemplateDao.getById(templateId) ?: return@withTransaction
            val updatedTemplate = currentTemplate.copy(
                title = input.title.trim(),
                note = input.note.trim(),
                durationText = input.durationText.trim(),
                startTime = input.startTime.trim(),
                endTime = input.endTime.trim()
            )
            taskTemplateDao.update(updatedTemplate)
            val dateKey = currentDate.value.toStorageKey()
            val todayRecord = dailyTaskRecordDao.getRecord(dateKey, templateId)
            if (todayRecord != null) {
                dailyTaskRecordDao.upsert(
                    todayRecord.copy(
                        titleSnapshot = updatedTemplate.title,
                        noteSnapshot = updatedTemplate.note,
                        durationTextSnapshot = updatedTemplate.durationText,
                        startTimeSnapshot = updatedTemplate.startTime,
                        endTimeSnapshot = updatedTemplate.endTime,
                        orderIndex = updatedTemplate.sortOrder
                    )
                )
            }
        }
    }

    suspend fun deleteTemplate(templateId: Long) {
        database.withTransaction {
            taskTemplateDao.deleteById(templateId)
            dailyTaskRecordDao.deleteByDateAndTemplateId(currentDate.value.toStorageKey(), templateId)
        }
    }

    suspend fun updateTaskCompletion(templateId: Long, isCompleted: Boolean) {
        dailyTaskRecordDao.updateCompletion(
            date = currentDate.value.toStorageKey(),
            templateId = templateId,
            isCompleted = isCompleted
        )
    }

    suspend fun wasCelebratedToday(): Boolean {
        val settings = settingsFlow.first()
        return settings.lastCelebratedDate == currentDate.value.toStorageKey()
    }

    suspend fun getCurrentLanguage(): UiLanguage {
        return settingsFlow.first().language
    }

    suspend fun markTodayCelebrated() {
        settingsStore.setLastCelebratedDate(currentDate.value.toStorageKey())
    }

    suspend fun setLanguage(language: UiLanguage) {
        settingsStore.setLanguage(language)
    }

    suspend fun clearAllData() {
        database.withTransaction {
            taskTemplateDao.clearAll()
            dailyTaskRecordDao.clearAll()
        }
        settingsStore.clearCelebrationState()
        refreshCurrentDate()
    }

    private suspend fun syncTodayRecords(today: LocalDate) {
        val dateKey = today.toStorageKey()
        database.withTransaction {
            val templates = taskTemplateDao.getAll()
            val currentRecords = dailyTaskRecordDao.getByDate(dateKey)
            val templatesById = templates.associateBy { it.id }
            val recordIds = currentRecords.map { it.templateId }.toSet()

            val newRecords = templates
                .filter { it.id !in recordIds }
                .map { template -> template.toDailyRecord(dateKey, isCompleted = false) }
            if (newRecords.isNotEmpty()) {
                dailyTaskRecordDao.upsertAll(newRecords)
            }

            val staleIds = currentRecords
                .filter { it.templateId !in templatesById.keys }
                .map { it.templateId }
            if (staleIds.isNotEmpty()) {
                dailyTaskRecordDao.deleteByDateAndTemplateIds(dateKey, staleIds)
            }

            val updatedSnapshots = currentRecords.mapNotNull { record ->
                val template = templatesById[record.templateId] ?: return@mapNotNull null
                val refreshed = record.copy(
                    titleSnapshot = template.title,
                    noteSnapshot = template.note,
                    durationTextSnapshot = template.durationText,
                    startTimeSnapshot = template.startTime,
                    endTimeSnapshot = template.endTime,
                    orderIndex = template.sortOrder
                )
                if (refreshed == record) null else refreshed
            }
            if (updatedSnapshots.isNotEmpty()) {
                dailyTaskRecordDao.upsertAll(updatedSnapshots)
            }
        }
    }

    private fun TaskTemplateEntity.toModel(): TaskTemplate {
        return TaskTemplate(
            id = id,
            title = title,
            note = note,
            durationText = durationText,
            startTime = startTime,
            endTime = endTime,
            sortOrder = sortOrder
        )
    }

    private fun TaskTemplateEntity.toDailyRecord(date: String, isCompleted: Boolean): DailyTaskRecordEntity {
        return DailyTaskRecordEntity(
            date = date,
            templateId = id,
            titleSnapshot = title,
            noteSnapshot = note,
            durationTextSnapshot = durationText,
            startTimeSnapshot = startTime,
            endTimeSnapshot = endTime,
            isCompleted = isCompleted,
            orderIndex = sortOrder
        )
    }

    private fun DailyTaskRecordEntity.toModel(): DailyTaskItem {
        return DailyTaskItem(
            date = LocalDate.parse(date),
            templateId = templateId,
            title = titleSnapshot,
            note = noteSnapshot,
            durationText = durationTextSnapshot,
            startTime = startTimeSnapshot,
            endTime = endTimeSnapshot,
            isCompleted = isCompleted,
            orderIndex = orderIndex
        )
    }

    private fun LocalDate.toStorageKey(): String = toString()
}
