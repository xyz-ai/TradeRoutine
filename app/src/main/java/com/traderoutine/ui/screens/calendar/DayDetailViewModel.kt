package com.traderoutine.ui.screens.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traderoutine.data.TradeRoutineRepository
import com.traderoutine.model.DailyStatus
import com.traderoutine.model.DailyTaskItem
import com.traderoutine.ui.navigation.Routes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

data class DayDetailUiState(
    val date: LocalDate = LocalDate.now(),
    val tasks: List<DailyTaskItem> = emptyList(),
    val status: DailyStatus = DailyStatus.NOT_STARTED,
) {
    val isEmpty: Boolean = tasks.isEmpty()
}

class DayDetailViewModel(
    repository: TradeRoutineRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val date = LocalDate.parse(checkNotNull(savedStateHandle.get<String>(Routes.DATE)))

    val uiState: StateFlow<DayDetailUiState> = repository.observeTasksForDate(date)
        .map { tasks ->
            DayDetailUiState(
                date = date,
                tasks = tasks.sortedBy { it.orderIndex },
                status = DailyStatus.fromCounts(
                    totalCount = tasks.size,
                    completedCount = tasks.count { it.isCompleted }
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DayDetailUiState(date = date)
        )
}
