package com.app.patientcareapp.feature_med_reminder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.patientcareapp.feature_med_reminder.data.local.entity.MedReminderEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface MedReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMedReminder(medReminder: MedReminderEntity)

    @Query("SELECT * FROM med_reminders")
    fun getAllMedReminders(): Flow<List<MedReminderEntity>>

    @Query("SELECT * FROM med_reminders WHERE id=:id")
    suspend fun getMedReminder(id: Int): MedReminderEntity?

    @Query("DELETE FROM med_reminders WHERE id=:id")
    suspend fun deleteMedReminder(id: Int)

    @Query("UPDATE med_reminders SET isActive = :isActive WHERE id = :id")
    suspend fun updateReminderStatus(id: Int, isActive: Boolean)

}