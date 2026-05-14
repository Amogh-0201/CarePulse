package com.app.patientcareapp.feature_health_records.domain.use_case

import com.app.patientcareapp.feature_health_records.domain.repository.HealthRecordRepository
import javax.inject.Inject

class GetAllHealthRecordsUseCase @Inject constructor(
    private val repository: HealthRecordRepository
) {

    operator fun invoke() = repository.getAllHealthRecords()
}