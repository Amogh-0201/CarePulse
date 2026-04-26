package com.app.patientcareapp.feature_med_reminder.domain.use_case

import com.app.patientcareapp.feature_med_reminder.domain.repository.MedReminderRepository
import javax.inject.Inject

class DeleteMedReminderUseCase @Inject constructor(
    private val repository: MedReminderRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.deleteMedReminder(id)
    }
}