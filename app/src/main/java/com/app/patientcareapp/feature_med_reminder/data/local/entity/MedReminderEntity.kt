package com.app.patientcareapp.feature_med_reminder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.patientcareapp.feature_med_reminder.domain.model.MedReminder

@Entity(tableName = "med_reminders")
data class MedReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val medicineName: String,
    val dosage: String,  // e.g. "1 tablet", "5ml"
    val times: List<String>,   // ["08:00", "14:00", "20:00"]
    val startDate: Long,    // timestamp
    val endDate: Long?,     // nullable if ongoing
    val repeatType: String,   // "DAILY", "WEEKLY", "CUSTOM"
    val isActive: Boolean = true,
    val notes: String? = null
) {
    fun toMedReminder(): MedReminder {
        return MedReminder(
            id = id,
            medicineName =  medicineName,
            dosage =  dosage,
            times =  times,
            startDate = startDate,
            endDate = endDate,
            repeatType = repeatType,
            isActive = isActive,
            notes = notes
        )
    }
}