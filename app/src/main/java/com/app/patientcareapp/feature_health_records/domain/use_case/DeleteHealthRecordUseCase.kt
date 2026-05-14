package com.app.patientcareapp.feature_health_records.domain.use_case

import com.app.patientcareapp.feature_health_records.domain.model.HealthRecord
import com.app.patientcareapp.feature_health_records.domain.repository.HealthRecordRepository
import javax.inject.Inject

class DeleteHealthRecordUseCase @Inject constructor(
    private val repository: HealthRecordRepository
) {

    suspend operator fun invoke(healthRecord: HealthRecord) {
        repository.deleteHealthRecord(healthRecord = healthRecord)
    }
}