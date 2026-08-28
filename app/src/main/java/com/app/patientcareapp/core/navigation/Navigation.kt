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
import androidx.compose.foundation.pager.rememberPagerState
import com.app.patientcareapp.feature_health_records.presentation.add_health_records.AddHealthRecordsScreen
import com.app.patientcareapp.feature_health_records.presentation.health_record_viewer.HealthRecordViewerScreen
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
    val pagerState = rememberPagerState(initialPage = 1) { 3 }

    val bottomBarScreens = listOf(
        Screen.Main.route
    )

    val currentRoute = navController.currentBackStackEntryFlow.collectAsState(initial = null)
        .value
        ?.destination
        ?.route

    Scaffold(
        bottomBar = {
            if(currentRoute in bottomBarScreens) {
                AppNavBar(navController = navController, pagerState = pagerState)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (startDestination == Screen.Home.route) Screen.Main.route else startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Main.route) {
                MainPagerScreen(
                    navController = navController,
                    paddingValues = innerPadding,
                    pagerState = pagerState,
                    onMedicineClick = { medId ->
                        navController.navigate("add_edit_med_reminder?id=$medId")
                    }
                )
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