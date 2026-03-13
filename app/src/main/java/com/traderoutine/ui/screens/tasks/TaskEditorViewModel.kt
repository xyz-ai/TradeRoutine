package com.traderoutine.ui.screens.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traderoutine.data.TradeRoutineRepository
import com.traderoutine.ui.navigation.Routes
import com.traderoutine.model.TaskTemplateInput
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TaskEditorUiState(
    val templateId: Long? = null,
    val title: String = "",
    val note: String = "",
    val durationText: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val titleError: Boolean = false,
    val isLoading: Boolean = true,
) {
    val isEditMode: Boolean = templateId != null
}

class TaskEditorViewModel(
    private val repository: TradeRoutineRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val templateIdArg = savedStateHandle.get<Long>(Routes.TEMPLATE_ID)?.takeIf { it >= 0L }
    private val mutableState = MutableStateFlow(TaskEditorUiState(templateId = templateIdArg))
    private val finishSignal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    val uiState: StateFlow<TaskEditorUiState> = mutableState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), mutableState.value)
    val finishEvents = finishSignal.asSharedFlow()

    init {
        viewModelScope.launch {
            if (templateIdArg == null) {
                mutableState.value = mutableState.value.copy(isLoading = false)
            } else {
                val template = repository.getTemplate(templateIdArg)
                mutableState.value = mutableState.value.copy(
                    title = template?.title.orEmpty(),
                    note = template?.note.orEmpty(),
                    durationText = template?.durationText.orEmpty(),
                    startTime = template?.startTime.orEmpty(),
                    endTime = template?.endTime.orEmpty(),
                    isLoading = false
                )
            }
        }
    }

    fun updateTitle(value: String) {
        mutableState.value = mutableState.value.copy(title = value, titleError = false)
    }

    fun updateNote(value: String) {
        mutableState.value = mutableState.value.copy(note = value)
    }

    fun updateDurationText(value: String) {
        mutableState.value = mutableState.value.copy(durationText = value)
    }

    fun updateStartTime(value: String) {
        mutableState.value = mutableState.value.copy(startTime = value)
    }

    fun updateEndTime(value: String) {
        mutableState.value = mutableState.value.copy(endTime = value)
    }

    fun save() {
        val currentState = mutableState.value
        if (currentState.title.isBlank()) {
            mutableState.value = currentState.copy(titleError = true)
            return
        }

        viewModelScope.launch {
            val input = TaskTemplateInput(
                title = currentState.title,
                note = currentState.note,
                durationText = currentState.durationText,
                startTime = currentState.startTime,
                endTime = currentState.endTime
            )
            if (currentState.templateId == null) {
                repository.addTemplate(input)
            } else {
                repository.updateTemplate(currentState.templateId, input)
            }
            finishSignal.tryEmit(Unit)
        }
    }
}
