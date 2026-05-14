package com.app.patientcareapp.feature_health_records.domain.model

import com.app.patientcareapp.feature_health_records.data.local.entity.HealthRecordEntity

data class HealthRecord(
    val id: Long = 0,
    val title: String,
    val category: RecordCategory,
    val fileUri: String,
    val fileType: FileType,
    val hospitalName: String? = null,
    val doctorName: String? = null,
    val date: Long,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {

    fun toHealthRecordEntity(): HealthRecordEntity {
        return HealthRecordEntity(
            id = id,
            title = title,
            category = category,
            fileUri = fileUri,
            fileType = fileType,
            hospitalName = hospitalName,
            doctorName = doctorName,
            date = date,
            notes = notes,
            createdAt = createdAt
        )
    }
}