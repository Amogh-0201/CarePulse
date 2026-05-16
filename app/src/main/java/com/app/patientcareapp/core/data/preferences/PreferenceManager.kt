package com.app.patientcareapp.core.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.app.patientcareapp.core.data.local.datastore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferenceManager(
    private val context: Context
) {

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val BATTERY_WARNING_DISMISSED = booleanPreferencesKey("battery_warning_dismissed")
    }

    val isOnBoardingCompleted: Flow<Boolean> = context.datastore.data
        .map {preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }

    suspend fun setOnBoardingCompleted(completed: Boolean) {
        context.datastore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    // Read state: Has user resolved/dismissed the warning?
    val isBatteryWarningDismissed: Flow<Boolean> = context.datastore.data
        .map { preferences ->
            preferences[BATTERY_WARNING_DISMISSED] ?: false
        }

    // Write state
    suspend fun setBatteryWarningDismissed(dismissed: Boolean) {
        context.datastore.edit { preferences ->
            preferences[BATTERY_WARNING_DISMISSED] = dismissed
        }
    }
}