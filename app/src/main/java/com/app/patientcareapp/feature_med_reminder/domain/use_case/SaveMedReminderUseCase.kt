package com.app.patientcareapp.feature_med_reminder.domain.use_case

import com.app.patientcareapp.feature_med_reminder.domain.model.MedReminder
import com.app.patientcareapp.feature_med_reminder.domain.repository.MedReminderRepository
import javax.inject.Inject

class SaveMedReminderUseCase @Inject constructor(
    private val repository: MedReminderRepository
) {
    suspend operator fun invoke(medReminder: MedReminder) {
        repository.saveMedReminder(medReminder)
    }
}