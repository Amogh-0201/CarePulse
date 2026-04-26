package com.app.patientcareapp.feature_med_reminder.data.repository

import com.app.patientcareapp.feature_med_reminder.data.local.dao.MedReminderDao
import com.app.patientcareapp.feature_med_reminder.domain.model.MedReminder
import com.app.patientcareapp.feature_med_reminder.domain.repository.MedReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MedReminderRepositoryImpl @Inject constructor(
    private val dao: MedReminderDao
): MedReminderRepository {

    override suspend fun saveMedReminder(medReminder: MedReminder) {
        dao.saveMedReminder(medReminder.toMedReminderEntity())
    }

    override fun getAllMedReminders(): Flow<List<MedReminder>> = flow {
        dao.getAllMedReminders().collect { medReminderEntities ->
            val medReminders = medReminderEntities.map {
                it.toMedReminder()
            }
            emit(medReminders)
        }
    }

    override suspend fun getMedReminder(id: Int): MedReminder? {
        val medReminder = dao.getMedReminder(id)
        return medReminder?.toMedReminder()
    }

    override suspend fun deleteMedReminder(id: Int) {
        dao.deleteMedReminder(id)
    }

    override suspend fun updateReminderStatus(id: Int, isActive: Boolean) {
        dao.updateReminderStatus(id, isActive)
    }
}