package com.app.patientcareapp.feature_med_reminder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.patientcareapp.feature_med_reminder.data.local.converters.Converters
import com.app.patientcareapp.feature_med_reminder.data.local.dao.MedReminderDao
import com.app.patientcareapp.feature_med_reminder.data.local.entity.MedReminderEntity

@Database(
    entities = [MedReminderEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MedReminderDataBase: RoomDatabase() {

    abstract val dao: MedReminderDao
}