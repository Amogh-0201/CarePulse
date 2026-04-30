package com.app.patientcareapp.feature_med_reminder.presentation.show_med_reminders

import androidx.lifecycle.ViewModel
import com.app.patientcareapp.feature_med_reminder.domain.use_case.MedReminderUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MedReminderViewModel @Inject constructor(
    private val useCases: MedReminderUseCases
): ViewModel() {

}