package com.app.patientcareapp.core.data.local

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.datastore by preferencesDataStore(
    name = "app_preferences"
)