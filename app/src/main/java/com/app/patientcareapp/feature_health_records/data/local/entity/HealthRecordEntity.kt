package com.app.patientcareapp.feature_health_records.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.patientcareapp.feature_health_records.domain.model.FileType
import com.app.patientcareapp.feature_health_records.domain.model.HealthRecord
import com.app.patientcareapp.feature_health_records.domain.model.RecordCategory


@Entity
data class HealthRecordEntity(
    @PrimaryKey(autoGenerate = true)
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

    fun toHealthRecord(): HealthRecord {
        return HealthRecord(
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