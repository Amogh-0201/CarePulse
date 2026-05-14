package com.app.patientcareapp.feature_health_records.domain.use_case

data class HealthRecordUseCases(
    val addHealthRecordUseCase: AddHealthRecordUseCase,
    val deleteHealthRecordUseCase: DeleteHealthRecordUseCase,
    val getAllHealthRecordsUseCase: GetAllHealthRecordsUseCase,
    val getHealthRecordByIdUseCase: GetHealthRecordByIdUseCase,
    val getHealthRecordsByCategoryUseCase: GetHealthRecordsByCategoryUseCase,
    val getRecentHealthRecordsUseCase: GetRecentHealthRecordsUseCase,
    val searchHealthRecordsUseCase: SearchHealthRecordsUseCase
)
