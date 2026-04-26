package com.app.patientcareapp.feature_med_reminder.domain.model

import com.app.patientcareapp.feature_med_reminder.data.local.entity.MedReminderEntity

data class MedReminder(
    val medicineName: String,
    val dosage: String,
    val times: List<String>,
    val startDate: Long,
    val endDate: Long?,
    val repeatType: String,
    val isActive: Boolean = true,
    val notes: String? = null
) {
    fun toMedReminderEntity(): MedReminderEntity {
        return MedReminderEntity(
            medicineName = medicineName,
            dosage = dosage,
            times = times,
            startDate = startDate,
            endDate = endDate,
            repeatType = repeatType,
            isActive = isActive,
            notes = notes
        )
    }
}