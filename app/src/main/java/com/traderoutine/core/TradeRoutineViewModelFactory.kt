package com.traderoutine.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.traderoutine.ui.screens.calendar.CalendarViewModel
import com.traderoutine.ui.screens.calendar.DayDetailViewModel
import com.traderoutine.ui.screens.settings.SettingsViewModel
import com.traderoutine.ui.screens.tasks.TaskEditorViewModel
import com.traderoutine.ui.screens.tasks.TasksViewModel

class TradeRoutineViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()
        return when {
            modelClass.isAssignableFrom(TasksViewModel::class.java) -> {
                TasksViewModel(container.repository) as T
            }
            modelClass.isAssignableFrom(TaskEditorViewModel::class.java) -> {
                TaskEditorViewModel(container.repository, savedStateHandle) as T
            }
            modelClass.isAssignableFrom(CalendarViewModel::class.java) -> {
                CalendarViewModel(container.repository) as T
            }
            modelClass.isAssignableFrom(DayDetailViewModel::class.java) -> {
                DayDetailViewModel(container.repository, savedStateHandle) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(container.repository) as T
            }
            else -> error("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
