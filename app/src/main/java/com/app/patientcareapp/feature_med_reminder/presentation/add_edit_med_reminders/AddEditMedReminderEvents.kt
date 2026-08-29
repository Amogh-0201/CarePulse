package com.app.patientcareapp.feature_med_reminder.presentation.add_edit_med_reminders

sealed class AddEditMedReminderEvents {

    data class OnNameChange(val name: String): AddEditMedReminderEvents()
    data class OnDosageChange(val dosage: String): AddEditMedReminderEvents()
    data class OnTimesChange(val times: List<String>): AddEditMedReminderEvents()
    data class OnStartDateChange(val startDate: Long): AddEditMedReminderEvents()
    data class OnEndDateChange(val endDate: Long?): AddEditMedReminderEvents()
    data class OnRepeatTypeChange(val repeatType: String): AddEditMedReminderEvents()
    data class OnIsActiveChange(val isActive: Boolean = true): AddEditMedReminderEvents()
    data class OnNotesChange(val notes: String? = null): AddEditMedReminderEvents()
    object OnSaveButtonClick: AddEditMedReminderEvents()
    data class OnDeleteTime(val time: String): AddEditMedReminderEvents()
    object OnFixAlarmPermission: AddEditMedReminderEvents()
}
