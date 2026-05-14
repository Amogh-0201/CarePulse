package com.app.patientcareapp.feature_health_records.di

import android.app.Application
import androidx.room.Room
import com.app.patientcareapp.feature_health_records.data.local.HealthRecordDataBase
import com.app.patientcareapp.feature_health_records.data.repository.HealthRecordRepositoryImpl
import com.app.patientcareapp.feature_health_records.domain.repository.HealthRecordRepository
import com.app.patientcareapp.feature_health_records.domain.use_case.AddHealthRecordUseCase
import com.app.patientcareapp.feature_health_records.domain.use_case.DeleteHealthRecordUseCase
import com.app.patientcareapp.feature_health_records.domain.use_case.GetAllHealthRecordsUseCase
import com.app.patientcareapp.feature_health_records.domain.use_case.GetHealthRecordByIdUseCase
import com.app.patientcareapp.feature_health_records.domain.use_case.GetHealthRecordsByCategoryUseCase
import com.app.patientcareapp.feature_health_records.domain.use_case.GetRecentHealthRecordsUseCase
import com.app.patientcareapp.feature_health_records.domain.use_case.HealthRecordUseCases
import com.app.patientcareapp.feature_health_records.domain.use_case.SearchHealthRecordsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object HealthRecordModule {

    @Provides
    @Singleton
    fun provideHealthRecordUseCases(repository: HealthRecordRepository): HealthRecordUseCases {
        return HealthRecordUseCases(
            addHealthRecordUseCase = AddHealthRecordUseCase(repository),
            deleteHealthRecordUseCase = DeleteHealthRecordUseCase(repository),
            getAllHealthRecordsUseCase = GetAllHealthRecordsUseCase(repository),
            getHealthRecordByIdUseCase = GetHealthRecordByIdUseCase(repository),
            getHealthRecordsByCategoryUseCase = GetHealthRecordsByCategoryUseCase(repository),
            getRecentHealthRecordsUseCase = GetRecentHealthRecordsUseCase(repository),
            searchHealthRecordsUseCase = SearchHealthRecordsUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideHealthRecordRepository(db: HealthRecordDataBase): HealthRecordRepository {
        return HealthRecordRepositoryImpl(db.dao)
    }

    @Provides
    @Singleton
    fun provideHealthRecordDataBase(app: Application): HealthRecordDataBase {
        return Room.databaseBuilder(
            context = app,
            klass = HealthRecordDataBase::class.java,
            name = "health_care_db"
        ).build()
    }

}