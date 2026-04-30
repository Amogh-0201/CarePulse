package com.app.patientcareapp.feature_med_reminder.presentation.add_edit_med_reminders

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.patientcareapp.feature_med_reminder.domain.model.MedReminder
import com.app.patientcareapp.feature_med_reminder.domain.use_case.MedReminderUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddEditMedReminderViewModel @Inject constructor(
    private val useCases: MedReminderUseCases
): ViewModel() {
    var name by mutableStateOf("")
        private set

    var dosage by mutableStateOf("")
        private set

    var times by mutableStateOf<List<String>>(emptyList())
        private set

    var startDate by mutableStateOf<Long?>(null)
        private set

    var endDate by mutableStateOf<Long?>(null)
        private set

    var repeatType by mutableStateOf("DAILY")
        private set

    var isActive by mutableStateOf<Boolean>(true)
        private set

    var notes by mutableStateOf("")
        private set


    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    sealed class UiEvent {
        class ShowError(val message: String? = "Unknown Error Occurred"): UiEvent()
        object SaveSuccess: UiEvent()
    }

    fun onEvent(event: AddEditMedReminderEvents) {
        when(event) {
            is AddEditMedReminderEvents.OnNameChange -> {
                name = event.name
            }
            is AddEditMedReminderEvents.OnDosageChange -> {
                dosage = event.dosage
            }
            is AddEditMedReminderEvents.OnTimesChange -> {
                times = event.times
            }
            is AddEditMedReminderEvents.OnStartDateChange -> {
                startDate = event.startDate
            }
            is AddEditMedReminderEvents.OnEndDateChange -> {
                endDate = event.endDate
            }
            is AddEditMedReminderEvents.OnRepeatTypeChange -> {
                repeatType = event.repeatType
            }
            is AddEditMedReminderEvents.OnIsActiveChange -> {
                isActive = event.isActive
            }
            is AddEditMedReminderEvents.OnNotesChange -> {
                notes = event.notes?: ""
            }
            is AddEditMedReminderEvents.OnSaveButtonClick -> {
                onSaveButtonClick()
            }
            is AddEditMedReminderEvents.OnDeleteTime -> {
                times = times.filter { it != event.time }
            }
        }
    }

    private fun onSaveButtonClick() {
        viewModelScope.launch {
            if(name.isBlank() || dosage.isBlank() || times.isEmpty() ||startDate == null) {
                _uiEvent.emit(UiEvent.ShowError("please fill all the required fields"))
                return@launch
            }
            try {
                useCases.saveMedReminderUseCase(
                    MedReminder(
                        name,
                        dosage,
                        times,
                        startDate!!,
                        endDate,
                        repeatType,
                        isActive,
                        notes
                    )
                )
                _uiEvent.emit(UiEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowError(e.message))
            }
        }
    }

}