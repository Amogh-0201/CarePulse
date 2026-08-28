package com.app.patientcareapp.feature_health_records.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.patientcareapp.feature_health_records.data.local.entity.HealthRecordEntity
import com.app.patientcareapp.feature_health_records.domain.model.RecordCategory
import kotlinx.coroutines.flow.Flow


@Dao
interface HealthRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecord(healthRecord: HealthRecordEntity)

    @Delete
    suspend fun deleteHealthRecord(healthRecord: HealthRecordEntity)

    @Query("SELECT * FROM healthrecordentity ORDER BY date DESC")
    fun getAllHealthRecords(): Flow<List<HealthRecordEntity>>

    @Query("SELECT * FROM healthrecordentity WHERE id = :id")
    suspend fun getHealthRecordById(id: Long): HealthRecordEntity?

    @Query("""
        SELECT * FROM healthrecordentity
        ORDER BY createdAt DESC
        LIMIT 5
    """)
    fun getRecentHealthRecords(): Flow<List<HealthRecordEntity>>

    @Query("""
        SELECT * FROM healthrecordentity
        WHERE category = :category
        ORDER BY date DESC
    """)
    fun getHealthRecordsByCategory(
        category: RecordCategory
    ): Flow<List<HealthRecordEntity>>

    @Query("""
    SELECT * FROM healthrecordentity
    WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%'
       OR LOWER(hospitalName) LIKE '%' || LOWER(:query) || '%'
       OR LOWER(doctorName) LIKE '%' || LOWER(:query) || '%'
    ORDER BY date DESC
""")
    fun searchHealthRecords(query: String): Flow<List<HealthRecordEntity>>

}