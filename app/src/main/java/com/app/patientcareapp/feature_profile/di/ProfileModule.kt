package com.app.patientcareapp.feature_profile.di

import android.app.Application
import androidx.room.Room
import com.app.patientcareapp.feature_profile.data.local.ProfileDataBase
import com.app.patientcareapp.feature_profile.data.repository.ProfileRepositoryImpl
import com.app.patientcareapp.feature_profile.domain.repository.ProfileRepository
import com.app.patientcareapp.feature_profile.domain.use_case.DeleteProfileUseCase
import com.app.patientcareapp.feature_profile.domain.use_case.GetProfileUseCase
import com.app.patientcareapp.feature_profile.domain.use_case.ProfileUseCases
import com.app.patientcareapp.feature_profile.domain.use_case.SaveProfileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideProfileUseCases(
        repository: ProfileRepository
    ): ProfileUseCases {
        return ProfileUseCases(
            saveProfileUseCase = SaveProfileUseCase(repository),
            getProfileUseCase = GetProfileUseCase(repository),
            deleteProfileUseCase = DeleteProfileUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideProfileRepository(db: ProfileDataBase): ProfileRepository {
        return ProfileRepositoryImpl(db.dao)
    }

    @Provides
    @Singleton
    fun provideProfileDataBase(app: Application): ProfileDataBase {
        return Room.databaseBuilder(
            context = app,
            klass = ProfileDataBase::class.java,
            name = "profile_db"
        ).build()
    }

}