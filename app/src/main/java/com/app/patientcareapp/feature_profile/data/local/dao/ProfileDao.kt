package com.app.patientcareapp.feature_profile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.patientcareapp.feature_profile.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: ProfileEntity)

    @Query("SELECT * FROM profile WHERE id=0")
    fun getProfile(): Flow<ProfileEntity?>

    @Query("DELETE FROM profile")
    suspend fun deleteProfile()
}