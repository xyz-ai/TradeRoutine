package com.traderoutine.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traderoutine.data.TradeRoutineRepository
import com.traderoutine.model.DailyStatus
import com.traderoutine.model.DailyTaskItem
import com.traderoutine.ui.strings.CompletionBlessings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TasksUiState(
    val date: LocalDate = LocalDate.now(),
    val tasks: List<DailyTaskItem> = emptyList(),
    val status: DailyStatus = DailyStatus.NOT_STARTED,
) {
    val totalCount: Int = tasks.size
    val completedCount: Int = tasks.count { it.isCompleted }
    val isEmpty: Boolean = tasks.isEmpty()
}

class TasksViewModel(
    private val repository: TradeRoutineRepository
) : ViewModel() {
    private val completionDialogMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<TasksUiState> = repository.observeCurrentDayTasks()
        .map { (date, tasks) ->
            TasksUiState(
                date = date,
                tasks = tasks,
                status = DailyStatus.fromCounts(
                    totalCount = tasks.size,
                    completedCount = tasks.count { it.isCompleted }
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TasksUiState()
        )

    val dialogMessage: StateFlow<String?> = completionDialogMessage.asStateFlow()

    fun onTaskCheckedChange(task: DailyTaskItem, isCompleted: Boolean) {
        val currentState = uiState.value
        if (task.isCompleted == isCompleted) return
        val completedCount = currentState.completedCount
        val afterCompletedCount = if (isCompleted) completedCount + 1 else completedCount - 1
        val reachedFullCompletion = isCompleted &&
            currentState.tasks.isNotEmpty() &&
            afterCompletedCount == currentState.tasks.size &&
            currentState.status != DailyStatus.COMPLETED

        viewModelScope.launch {
            repository.updateTaskCompletion(task.templateId, isCompleted)
            if (reachedFullCompletion && !repository.wasCelebratedToday()) {
                val language = repository.getCurrentLanguage()
                repository.markTodayCelebrated()
                completionDialogMessage.value = CompletionBlessings.random(language)
            }
        }
    }

    fun deleteTemplate(templateId: Long) {
        viewModelScope.launch {
            repository.deleteTemplate(templateId)
        }
    }

    fun dismissDialog() {
        completionDialogMessage.value = null
    }
}
