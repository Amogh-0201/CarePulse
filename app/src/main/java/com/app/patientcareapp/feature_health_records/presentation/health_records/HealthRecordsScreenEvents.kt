package com.app.patientcareapp.feature_health_records.presentation.health_records

import com.app.patientcareapp.feature_health_records.domain.model.HealthRecord
import com.app.patientcareapp.feature_health_records.domain.model.RecordCategory

sealed class HealthRecordsScreenEvents {

    data class OnSearchQueryChange(val query: String): HealthRecordsScreenEvents()
    data class OnCategorySelected(val category: RecordCategory?): HealthRecordsScreenEvents()
    data class OnHealthRecordClick(val healthRecord: HealthRecord): HealthRecordsScreenEvents()
    data class OnDeleteHealthRecord(val healthRecord: HealthRecord): HealthRecordsScreenEvents()
    object OnAddHealthRecordClick: HealthRecordsScreenEvents()
}