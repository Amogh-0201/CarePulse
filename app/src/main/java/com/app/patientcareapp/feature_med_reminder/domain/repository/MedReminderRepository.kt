package com.app.patientcareapp.feature_med_reminder.domain.repository

import com.app.patientcareapp.feature_med_reminder.domain.model.MedReminder
import kotlinx.coroutines.flow.Flow

interface MedReminderRepository {

    suspend fun saveMedReminder(medReminder: MedReminder): Long

    fun getAllMedReminders(): Flow<List<MedReminder>>

    suspend fun getMedReminder(id: Int): MedReminder?

    suspend fun deleteMedReminder(id: Int)

    suspend fun updateReminderStatus(id: Int, isActive: Boolean)
}