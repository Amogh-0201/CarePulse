package com.app.patientcareapp.feature_med_reminder.presentation.add_edit_med_reminders

import android.app.Application
import android.icu.util.Calendar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.patientcareapp.feature_med_reminder.data.alarm.MedReminderAlarmManager
import com.app.patientcareapp.feature_med_reminder.data.alarm.MedicineAlarmScheduler
import com.app.patientcareapp.feature_med_reminder.domain.model.MedReminder
import com.app.patientcareapp.feature_med_reminder.domain.use_case.MedReminderUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddEditMedReminderViewModel @Inject constructor(
    private val useCases: MedReminderUseCases,
    application: Application,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val alarmManager = MedReminderAlarmManager(
        MedicineAlarmScheduler(application)
    )

    private var currentId: Int? = null

    init {
        val id = savedStateHandle.get<Int>("id") ?: -1
        if (id != -1) {
            loadReminder(id = id)
        }
    }

    var heading: String? = "Add Medicine Reminder"

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
                // Set time to 23:59:59 to include the last day's doses
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = event.endDate ?: 0L
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }
                endDate = calendar.timeInMillis
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

                if (currentId != null) {
                    // Retrieve the prior version from DB to capture previous time counts
                    val oldReminder = useCases.getMedReminderUseCase(currentId!!)
                    oldReminder?.let { alarmManager.cancelMedReminder(it) }
                }

                val reminder = MedReminder(
                    id = currentId,
                    medicineName =name,
                    dosage = dosage,
                    times = times,
                    startDate = startDate!!,
                    endDate = endDate,
                    repeatType = repeatType,
                    isActive = isActive,
                    notes = notes
                )

                val insertedId = useCases.saveMedReminderUseCase(reminder)

                val savedReminder = reminder.copy(
                    id = insertedId.toInt()
                )

                if (savedReminder.isActive) {
                    alarmManager.scheduleMedReminder(savedReminder)
                }

                _uiEvent.emit(UiEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowError(e.message))
            }
        }
    }

    private fun loadReminder(id: Int) {
        viewModelScope.launch {
            val reminder = useCases.getMedReminderUseCase(id)
            reminder?.let {
                currentId = it.id
                heading = "Edit Medicine Reminder"
                name = it.medicineName
                dosage = it.dosage
                times = it.times
                startDate = it.startDate
                endDate = it.endDate
                repeatType = it.repeatType
                isActive = it.isActive
                notes = it.notes ?: ""
            }
        }
    }

}