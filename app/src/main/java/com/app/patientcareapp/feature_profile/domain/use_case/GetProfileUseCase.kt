package com.app.patientcareapp.feature_profile.domain.use_case

import com.app.patientcareapp.feature_profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {

    operator fun invoke() = repository.getProfile()
}