package com.app.patientcareapp.feature_med_reminder.presentation.show_med_reminders

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.patientcareapp.feature_med_reminder.data.alarm.MedReminderAlarmManager
import com.app.patientcareapp.feature_med_reminder.data.alarm.MedicineAlarmScheduler
import com.app.patientcareapp.feature_med_reminder.domain.model.MedReminder
import com.app.patientcareapp.feature_med_reminder.domain.use_case.MedReminderUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedReminderViewModel @Inject constructor(
    private val useCases: MedReminderUseCases,
    private val application: Application
): ViewModel() {

    val medReminders = useCases.getAllMedRemindersUseCase()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var deletedMedReminder: MedReminder? = null

    private val alarmManager = MedReminderAlarmManager(MedicineAlarmScheduler(application))

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
                viewModelScope.launch(Dispatchers.IO) {
                    deletedMedReminder = useCases.getMedReminderUseCase(event.id)
                    deletedMedReminder?.let{
                        alarmManager.cancelMedReminder(it)
                    }
                    useCases.deleteMedReminderUseCase(event.id)
                    _uiEvent.send(UiEvent.ShowSnackBar(message = "Med Reminder Deleted", action = "undo"))
                }
            }
            is MedReminderScreenEvents.OnUndoDeleteMedReminderClick -> {
                deletedMedReminder?.let { reminder ->
                    viewModelScope.launch {
                        useCases.saveMedReminderUseCase(reminder)
                        if(reminder.isActive) {
                            alarmManager.scheduleMedReminder(reminder)
                        }
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