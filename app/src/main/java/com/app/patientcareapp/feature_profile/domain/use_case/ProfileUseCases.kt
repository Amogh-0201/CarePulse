package com.app.patientcareapp.feature_profile.domain.use_case

data class ProfileUseCases(
    val saveProfileUseCase: SaveProfileUseCase,
    val getProfileUseCase: GetProfileUseCase,
    val deleteProfileUseCase: DeleteProfileUseCase
)