package com.app.patientcareapp.feature_profile.data.repository

import com.app.patientcareapp.feature_profile.data.local.dao.ProfileDao
import com.app.patientcareapp.feature_profile.domain.model.Profile
import com.app.patientcareapp.feature_profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val dao: ProfileDao
): ProfileRepository {
    override suspend fun saveProfile(profile: Profile) {
        dao.saveProfile(profile.toProfileEntity())
    }

    override fun getProfile(): Flow<Profile?> = dao.getProfile().map {
        it?.toProfile()
    }

    override suspend fun deleteProfile() {
        dao.deleteProfile()
    }
}