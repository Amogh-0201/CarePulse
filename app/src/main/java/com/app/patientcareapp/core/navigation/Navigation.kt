package com.app.patientcareapp.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.navigation
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.patientcareapp.core.util.Screen
import com.app.patientcareapp.feature_health_records.presentation.add_health_records.AddHealthRecordsScreen
import com.app.patientcareapp.feature_health_records.presentation.health_record_viewer.HealthRecordViewerScreen
import com.app.patientcareapp.feature_health_records.presentation.health_records.HealthRecordsScreen
import com.app.patientcareapp.feature_home.presentation.HomeScreen
import com.app.patientcareapp.feature_med_reminder.presentation.add_edit_med_reminders.AddEditMedReminderScreen
import com.app.patientcareapp.feature_med_reminder.presentation.show_med_reminders.MedReminderScreen
import com.app.patientcareapp.feature_onboarding.presentation.screens.AskAllergiesScreen
import com.app.patientcareapp.feature_onboarding.presentation.screens.AskConditionsScreen
import com.app.patientcareapp.feature_onboarding.presentation.screens.AskVitalsScreen
import com.app.patientcareapp.feature_onboarding.presentation.screens.BasicInfoScreen
import com.app.patientcareapp.feature_onboarding.presentation.screens.WelcomeScreen
import com.app.patientcareapp.feature_profile.presentation.edit_profile.EditProfileScreen
import com.app.patientcareapp.feature_profile.presentation.profile.ProfileScreen

@Composable
fun Navigation(
    startDestination: String
) {
    val navController = rememberNavController()

    val bottomBarScreens = listOf(
        Screen.Home.route,
        Screen.MedReminder.route,
        Screen.HealthRecords.route,
        Screen.Profile.route
    )

    val currentRoute = navController.currentBackStackEntryFlow.collectAsState(initial = null)
        .value
        ?.destination
        ?.route

    Scaffold(
        bottomBar = {
            if(currentRoute in bottomBarScreens) {
                AppNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    paddingValues = innerPadding,
                    onMedicineClick = { medId ->
                        navController.navigate("add_edit_med_reminder?id=$medId")
                    }
                )
            }
            composable(Screen.MedReminder.route) {
                MedReminderScreen(navController = navController)
            }
            composable(Screen.HealthRecords.route) {
                HealthRecordsScreen(navController = navController)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController)
            }
            composable(
                route = "add_edit_med_reminder?id={id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) {
                AddEditMedReminderScreen(modifier = Modifier, navController = navController)
            }
            composable("edit_profile") {
                EditProfileScreen(navController = navController)
            }
            composable("add_health_record") {
                AddHealthRecordsScreen(navController = navController)
            }
            composable(
                route = "health_record_viewer/{id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L

                HealthRecordViewerScreen(
                    navController = navController,
                    recordId = id
                )
            }

            navigation(
                route = "onboarding",
                startDestination = "welcome_screen"
            ) {

                composable("welcome_screen") {
                    WelcomeScreen(navController = navController)
                }

                composable("basic_info_screen") {
                    BasicInfoScreen(navController = navController)
                }

                composable("ask_conditions_screen") {
                    AskConditionsScreen(navController)
                }

                composable("ask_vitals_screen") {
                    AskVitalsScreen(navController)
                }

                composable("ask_allergies_screen") {
                    AskAllergiesScreen(navController)
                }
            }
        }
    }
}