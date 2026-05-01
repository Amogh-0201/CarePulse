package com.app.patientcareapp.feature_med_reminder.presentation.show_med_reminders

sealed class MedReminderScreenEvents {

    object OnAddMedReminderClick: MedReminderScreenEvents()
    data class OnDeleteMedReminderClick(val id: Int): MedReminderScreenEvents()
    data class OnMedReminderClick(val id: Int): MedReminderScreenEvents()
    object OnUndoDeleteMedReminderClick: MedReminderScreenEvents()
}