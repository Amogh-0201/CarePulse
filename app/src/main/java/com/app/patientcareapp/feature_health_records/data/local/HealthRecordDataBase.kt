package com.app.patientcareapp.feature_health_records.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.patientcareapp.feature_health_records.data.local.converters.HealthRecordConverters
import com.app.patientcareapp.feature_health_records.data.local.dao.HealthRecordDao
import com.app.patientcareapp.feature_health_records.data.local.entity.HealthRecordEntity


@Database(
    entities = [HealthRecordEntity::class],
    version = 1
)
@TypeConverters(HealthRecordConverters::class)
abstract class HealthRecordDataBase: RoomDatabase() {

    abstract val dao: HealthRecordDao
}