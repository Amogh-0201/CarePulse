package com.app.patientcareapp.feature_health_records.domain.use_case

import com.app.patientcareapp.feature_health_records.domain.repository.HealthRecordRepository
import javax.inject.Inject

class GetRecentHealthRecordsUseCase @Inject constructor(
    private val repository: HealthRecordRepository
) {

    operator fun invoke() = repository.getRecentHealthRecords()
}