package com.app.patientcareapp.feature_profile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.patientcareapp.feature_profile.data.local.converters.Converters
import com.app.patientcareapp.feature_profile.data.local.dao.ProfileDao
import com.app.patientcareapp.feature_profile.data.local.entity.ProfileEntity


@Database(
    entities = [ProfileEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ProfileDataBase: RoomDatabase() {

    abstract val dao: ProfileDao
}