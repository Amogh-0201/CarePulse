package com.app.patientcareapp.core.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Profile: Screen("profile", "Profile", Icons.Default.AccountCircle)
    object Home: Screen("home", "Home", Icons.Default.Home)
    object MedReminder: Screen("med_reminder","Med Reminder", Icons.Default.Medication)
    object HealthRecords: Screen("health_records", "Health Records", Icons.Default.Description)
}