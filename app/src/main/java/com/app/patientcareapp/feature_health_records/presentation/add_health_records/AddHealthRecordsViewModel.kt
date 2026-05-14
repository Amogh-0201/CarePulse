package com.app.patientcareapp.feature_health_records.presentation.add_health_records

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.patientcareapp.feature_health_records.domain.model.FileType
import com.app.patientcareapp.feature_health_records.domain.model.HealthRecord
import com.app.patientcareapp.feature_health_records.domain.model.RecordCategory
import com.app.patientcareapp.feature_health_records.domain.use_case.HealthRecordUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddHealthRecordsViewModel @Inject constructor(
    private val useCases: HealthRecordUseCases
): ViewModel() {

    var title by mutableStateOf("")
        private set

    var category: RecordCategory by mutableStateOf(RecordCategory.OTHER)
        private set

    var fileUri by mutableStateOf("")
        private set

    var fileType: FileType by mutableStateOf(FileType.PDF)
        private set

    var hospitalName by mutableStateOf("")
        private set

    var doctorName by mutableStateOf("")
        private set

    var date by mutableLongStateOf(System.currentTimeMillis())
        private set

    var notes by mutableStateOf("")
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    sealed class UiEvent {
        data class ShowSnackBar(val message: String): UiEvent()
        object NavigateBack: UiEvent()
    }

    fun onEvent(event: AddHealthRecordsEvents) {
        when(event) {
            is AddHealthRecordsEvents.OnTitleChange -> {
                title = event.title
            }
            is AddHealthRecordsEvents.OnCategoryChange -> {
                category = event.category
            }
            is AddHealthRecordsEvents.OnDoctorNameChange -> {
                doctorName = event.doctorName
            }
            is AddHealthRecordsEvents.OnHospitalNameChange -> {
                hospitalName = event.hospitalName
            }
            is AddHealthRecordsEvents.OnDateChange -> {
                date = event.date
            }
            is AddHealthRecordsEvents.OnNotesChange -> {
                notes = event.notes
            }
            is AddHealthRecordsEvents.OnFilePicked -> {
                fileUri = event.uri
                fileType = event.fileType
            }
            is AddHealthRecordsEvents.OnSaveHealthRecord -> {
                viewModelScope.launch {
                    try {
                        saveHealthRecord()
                        _uiEvent.send(UiEvent.NavigateBack)
                    } catch(e: Exception) {
                        _uiEvent.send(UiEvent.ShowSnackBar(e.message?: "Something went wrong"))
                    }
                }
            }
        }
    }

    private suspend fun saveHealthRecord() {

        val healthRecord = HealthRecord(
            title = title,
            category = category,
            fileUri = fileUri,
            fileType = fileType,
            hospitalName = hospitalName,
            doctorName = doctorName,
            date = date,
            notes = notes
        )

        useCases.addHealthRecordUseCase( healthRecord )
    }

    fun healthRecordInfoValid(): Boolean {
        return (title.isNotBlank() &&
                fileUri.isNotBlank())
    }

}