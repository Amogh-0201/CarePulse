package com.app.patientcareapp.feature_health_records.presentation.health_record_viewer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.patientcareapp.feature_health_records.domain.model.HealthRecord
import com.app.patientcareapp.feature_health_records.domain.use_case.HealthRecordUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HealthRecordViewerViewModel @Inject constructor(
    private val useCases: HealthRecordUseCases
): ViewModel() {

    var healthRecord by mutableStateOf<HealthRecord?>(null)
        private set

    fun loadRecord(id: Long) {
        viewModelScope.launch {
            healthRecord = useCases.getHealthRecordByIdUseCase(id = id)
        }
    }

}