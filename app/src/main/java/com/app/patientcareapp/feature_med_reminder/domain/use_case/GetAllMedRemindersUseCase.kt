package com.app.patientcareapp.feature_med_reminder.domain.use_case

import com.app.patientcareapp.feature_med_reminder.domain.model.MedReminder
import com.app.patientcareapp.feature_med_reminder.domain.repository.MedReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMedRemindersUseCase @Inject constructor(
    private val repository: MedReminderRepository
) {
    operator fun invoke(): Flow<List<MedReminder>> = repository.getAllMedReminders()
}