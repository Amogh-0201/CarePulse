package com.app.patientcareapp.feature_health_records.data.repository

import com.app.patientcareapp.feature_health_records.data.local.dao.HealthRecordDao
import com.app.patientcareapp.feature_health_records.domain.model.HealthRecord
import com.app.patientcareapp.feature_health_records.domain.model.RecordCategory
import com.app.patientcareapp.feature_health_records.domain.repository.HealthRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HealthRecordRepositoryImpl @Inject constructor(
    private val dao: HealthRecordDao
): HealthRecordRepository {

    override suspend fun insertHealthRecord(healthRecord: HealthRecord) {
        dao.insertHealthRecord(healthRecord.toHealthRecordEntity())
    }

    override suspend fun deleteHealthRecord(healthRecord: HealthRecord) {
        dao.deleteHealthRecord(healthRecord.toHealthRecordEntity())
    }

    override fun getAllHealthRecords(): Flow<List<HealthRecord>> = flow {
        dao.getAllHealthRecords().collect { healthRecordEntities ->
            val healthRecords = healthRecordEntities.map {
                it.toHealthRecord()
            }
            emit(healthRecords)
        }
    }

    override suspend fun getHealthRecordById(id: Long): HealthRecord? {
        return dao.getHealthRecordById(id = id)?.toHealthRecord()
    }

    override fun getRecentHealthRecords(): Flow<List<HealthRecord>> = flow {
        dao.getRecentHealthRecords().collect {healthRecordEntities ->
            val recentHealthRecords = healthRecordEntities.map {
                it.toHealthRecord()
            }
            emit(recentHealthRecords)
        }
    }

    override fun getHealthRecordsByCategory(category: RecordCategory): Flow<List<HealthRecord>> = flow {
        dao.getHealthRecordsByCategory(category = category).collect { healthRecordEntities ->
            val healthRecords = healthRecordEntities.map {
                it.toHealthRecord()
            }
            emit(healthRecords)
        }
    }

    override fun searchHealthRecords(query: String): Flow<List<HealthRecord>> = flow {
        dao.searchHealthRecords(query = query).collect {healthRecordEntities ->
            val healthRecords = healthRecordEntities.map {
                it.toHealthRecord()
            }
            emit(healthRecords)
        }
    }

}