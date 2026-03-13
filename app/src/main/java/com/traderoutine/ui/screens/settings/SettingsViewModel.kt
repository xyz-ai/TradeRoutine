package com.traderoutine.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traderoutine.data.TradeRoutineRepository
import com.traderoutine.model.UiLanguage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val language: UiLanguage = UiLanguage.ENGLISH
)

class SettingsViewModel(
    private val repository: TradeRoutineRepository
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = repository.settingsFlow
        .map { settings -> SettingsUiState(language = settings.language) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState()
        )

    fun setLanguage(language: UiLanguage) {
        viewModelScope.launch {
            repository.setLanguage(language)
        }
    }

    fun clearLocalData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }
}
