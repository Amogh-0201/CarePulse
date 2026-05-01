package com.app.patientcareapp.feature_med_reminder.presentation.show_med_reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.patientcareapp.feature_med_reminder.domain.model.MedReminder
import com.app.patientcareapp.feature_med_reminder.domain.use_case.MedReminderUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedReminderViewModel @Inject constructor(
    private val useCases: MedReminderUseCases
): ViewModel() {

    val medReminders = useCases.getAllMedRemindersUseCase()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var deletedMedReminder: MedReminder? = null

    fun onEvent(event: MedReminderScreenEvents) {
        when(event) {
            is MedReminderScreenEvents.OnAddMedReminderClick -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.NavigateToAddMedReminder)
                }
            }
            is MedReminderScreenEvents.OnMedReminderClick -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.NavigateToEditMedReminder(event.id))
                }
            }
            is MedReminderScreenEvents.OnDeleteMedReminderClick -> {
                viewModelScope.launch {
                    deletedMedReminder = useCases.getMedReminderUseCase(event.id)
                    useCases.deleteMedReminderUseCase(event.id)
                    _uiEvent.send(UiEvent.ShowSnackBar(message = "Med Reminder Deleted", action = "undo"))
                }
            }
            is MedReminderScreenEvents.OnUndoDeleteMedReminderClick -> {
                deletedMedReminder?.let {
                    viewModelScope.launch {
                        useCases.saveMedReminderUseCase(deletedMedReminder!!)
                    }
                }
            }
        }
    }

    sealed class UiEvent {

        data class ShowSnackBar(val message: String = "Med Reminder Deleted", val action: String? = null): UiEvent()
        object NavigateToAddMedReminder: UiEvent()
        data class NavigateToEditMedReminder(val id: Int): UiEvent()
    }

}