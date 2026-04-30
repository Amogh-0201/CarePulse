package com.app.patientcareapp.feature_med_reminder.di

import android.app.Application
import androidx.room.Room
import com.app.patientcareapp.feature_med_reminder.data.local.MedReminderDataBase
import com.app.patientcareapp.feature_med_reminder.data.local.converters.Converters
import com.app.patientcareapp.feature_med_reminder.data.repository.MedReminderRepositoryImpl
import com.app.patientcareapp.feature_med_reminder.domain.repository.MedReminderRepository
import com.app.patientcareapp.feature_med_reminder.domain.use_case.DeleteMedReminderUseCase
import com.app.patientcareapp.feature_med_reminder.domain.use_case.GetAllMedRemindersUseCase
import com.app.patientcareapp.feature_med_reminder.domain.use_case.GetMedReminderUseCase
import com.app.patientcareapp.feature_med_reminder.domain.use_case.MedReminderUseCases
import com.app.patientcareapp.feature_med_reminder.domain.use_case.SaveMedReminderUseCase
import com.app.patientcareapp.feature_med_reminder.domain.use_case.UpdateReminderStatusUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MedReminderModule {

    @Provides
    @Singleton
    fun provideMedReminderUseCases(
        repository: MedReminderRepository
    ): MedReminderUseCases {
        return MedReminderUseCases(
            saveMedReminderUseCase = SaveMedReminderUseCase(repository),
            getAllMedRemindersUseCase = GetAllMedRemindersUseCase(repository),
            getMedReminderUseCase = GetMedReminderUseCase(repository),
            deleteMedReminderUseCase = DeleteMedReminderUseCase(repository),
            updateReminderStatusUseCase = UpdateReminderStatusUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideMedReminderRepository(
        db: MedReminderDataBase
    ): MedReminderRepository {
        return MedReminderRepositoryImpl(db.dao)
    }

    @Provides
    @Singleton
    fun provideMedReminderDataBase(
        app: Application
    ): MedReminderDataBase {
        return Room.databaseBuilder(
            context = app,
            klass = MedReminderDataBase::class.java,
            name = "med_reminder_db"
        ).build()
    }

}