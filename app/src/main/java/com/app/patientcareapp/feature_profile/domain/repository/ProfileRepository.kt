package com.app.patientcareapp.feature_profile.domain.repository

import com.app.patientcareapp.feature_profile.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun saveProfile(profile: Profile)

    fun getProfile(): Flow<Profile?>

    suspend fun deleteProfile()
}