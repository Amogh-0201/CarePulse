package com.app.patientcareapp.feature_profile.domain.use_case

import com.app.patientcareapp.feature_profile.domain.repository.ProfileRepository
import javax.inject.Inject

class DeleteProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke() {
        repository.deleteProfile()
    }
}