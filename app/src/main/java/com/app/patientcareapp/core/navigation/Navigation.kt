package com.app.patientcareapp.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.patientcareapp.core.util.Screen
import com.app.patientcareapp.feature_health_records.presentation.HealthRecordsScreen
import com.app.patientcareapp.feature_home.presentation.HomeScreen
import com.app.patientcareapp.feature_med_reminder.presentation.add_edit_med_reminders.AddEditMedReminderScreen
import com.app.patientcareapp.feature_med_reminder.presentation.show_med_reminders.MedReminderScreen
import com.app.patientcareapp.feature_profile.presentation.ProfileScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {AppNavBar(navController = navController)}
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(it)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.MedReminder.route) {
                AddEditMedReminderScreen(modifier = Modifier)
            }
            composable(Screen.HealthRecords.route) {
                HealthRecordsScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}