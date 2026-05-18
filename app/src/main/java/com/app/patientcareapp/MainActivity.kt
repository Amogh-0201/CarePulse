package com.app.patientcareapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.app.patientcareapp.core.navigation.Navigation
import com.app.patientcareapp.core.presentation.BrandedSplashScreen
import com.app.patientcareapp.feature_med_reminder.data.alarm.MedicineAlarmScheduler
import com.app.patientcareapp.ui.theme.PatientCareAppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Hand over control to Compose immediately
        splashScreen.setKeepOnScreenCondition { false }

        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }

        setContent {
            PatientCareAppTheme {

                val startDestination by viewModel.startDestination.collectAsState()

                if (!viewModel.splashFinished || startDestination == null) {
                    BrandedSplashScreen(
                        onAnimationFinished = { viewModel.setSplashScreenFinished() }
                    )
                } else {
                    Navigation(startDestination = startDestination!!)
                }
            }
        }
    }
}