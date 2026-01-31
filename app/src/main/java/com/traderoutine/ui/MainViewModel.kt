package com.traderoutine.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.traderoutine.data.DayRecord
import com.traderoutine.data.MoodStatus
import com.traderoutine.data.ModuleCatalog
import com.traderoutine.data.SettingsEntity
import com.traderoutine.data.TradeRoutineRepository
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: TradeRoutineRepository,
) : ViewModel() {
    private val today = MutableStateFlow(LocalDate.now())

    val todayRecord = today.flatMapLatest { repository.recordForDate(it) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            DayRecord(
                date = LocalDate.now(),
                moduleItemStates = ModuleCatalog.defaultModuleStates(),
                moodStatus = null,
            ),
        )

    val settings = repository.settings()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SettingsEntity(
                reminderEnabled = false,
                darkModeEnabled = false,
                includeWeekend = true,
            ),
        )

    fun toggleItem(moduleIndex: Int, itemIndex: Int, checked: Boolean) {
        val current = todayRecord.value
        val updatedStates = current.moduleItemStates.mapIndexed { mIndex, items ->
            if (mIndex != moduleIndex) {
                items
            } else {
                items.mapIndexed { iIndex, value ->
                    if (iIndex == itemIndex) checked else value
                }
            }
        }
        updateRecord(current.copy(moduleItemStates = updatedStates))
    }

    fun updateMood(moodStatus: MoodStatus?) {
        val current = todayRecord.value
        updateRecord(current.copy(moodStatus = moodStatus))
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun updateSettings(updater: (SettingsEntity) -> SettingsEntity) {
        viewModelScope.launch {
            repository.updateSettings(updater(settings.value))
        }
    }

    fun recordsForMonth(month: YearMonth): Flow<List<DayRecord>> = repository.recordsForMonth(month)

    fun recordsBetween(start: LocalDate, end: LocalDate): Flow<List<DayRecord>> =
        repository.recordsBetween(start, end)

    private fun updateRecord(record: DayRecord) {
        viewModelScope.launch {
            repository.updateRecord(record)
        }
    }
}

class MainViewModelFactory(
    private val repository: TradeRoutineRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
