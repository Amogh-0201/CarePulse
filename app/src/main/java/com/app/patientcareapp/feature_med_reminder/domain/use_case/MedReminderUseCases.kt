package com.app.patientcareapp.feature_med_reminder.domain.use_case


data class MedReminderUseCases(
    val saveMedReminderUseCase: SaveMedReminderUseCase,
    val getAllMedRemindersUseCase: GetAllMedRemindersUseCase,
    val getMedReminderUseCase: GetMedReminderUseCase,
    val deleteMedReminderUseCase: DeleteMedReminderUseCase,
    val updateReminderStatusUseCase: UpdateReminderStatusUseCase
)