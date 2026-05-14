package com.app.patientcareapp.feature_health_records.domain.repository

import com.app.patientcareapp.feature_health_records.domain.model.HealthRecord
import com.app.patientcareapp.feature_health_records.domain.model.RecordCategory
import kotlinx.coroutines.flow.Flow

interface HealthRecordRepository {

    suspend fun insertHealthRecord(healthRecord: HealthRecord)

    suspend fun deleteHealthRecord(healthRecord: HealthRecord)

    fun getAllHealthRecords(): Flow<List<HealthRecord>>

    suspend fun getHealthRecordById(id: Long): HealthRecord?

    fun getRecentHealthRecords(): Flow<List<HealthRecord>>

    fun getHealthRecordsByCategory(category: RecordCategory): Flow<List<HealthRecord>>

    fun searchHealthRecords(query: String): Flow<List<HealthRecord>>

}