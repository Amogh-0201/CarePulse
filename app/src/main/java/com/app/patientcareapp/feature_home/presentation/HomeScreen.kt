package com.app.patientcareapp.feature_home.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.app.patientcareapp.core.util.BatteryOptimizationHelper
import com.app.patientcareapp.ui.theme.PrimaryBlue
import com.app.patientcareapp.ui.theme.SecondaryTeal

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
    onMedicineClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val isPreferenceDismissed by viewModel.isBatteryWarningDismissed.collectAsState()
    var isSystemOptimizingBattery by remember { mutableStateOf(false) }

    // Logic to detect loading: if userName is blank and no medicines yet, we are likely loading
    val isLoading = viewModel.userName.isBlank() && viewModel.todayMedicines.isEmpty()

    LaunchedEffect(Unit) {
        isSystemOptimizingBattery = !BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context)
    }

    // --- Premium Mesh Background ---
    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
            MaterialTheme.colorScheme.background
        ),
        startY = 0f,
        endY = 1000f
    )

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
        Crossfade(targetState = isLoading, animationSpec = tween(600)) { loading ->
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 3.dp, modifier = Modifier.size(40.dp))
                }
            } else {
                HomeScreenContent(
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    isSystemOptimizingBattery = isSystemOptimizingBattery,
                    isPreferenceDismissed = isPreferenceDismissed,
                    onMedicineClick = onMedicineClick
                )
            }
        }
    }
}

@Composable
private fun HomeScreenContent(
    viewModel: HomeViewModel,
    paddingValues: PaddingValues,
    isSystemOptimizingBattery: Boolean,
    isPreferenceDismissed: Boolean,
    onMedicineClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = paddingValues.calculateTopPadding() + 16.dp,
            bottom = paddingValues.calculateBottomPadding() + 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Premium Header
        item { HeaderSection(userName = viewModel.userName) }

        if (isSystemOptimizingBattery && !isPreferenceDismissed) {
            item { BatteryWarningCard(onDismiss = { viewModel.dismissBatteryWarning() }) }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HealthSummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    bloodGroup = viewModel.bloodGroup,
                    recordCount = viewModel.totalHealthRecords
                )

                UpcomingMedicineMiniCard(
                    modifier = Modifier.weight(1.2f),
                    upcoming = viewModel.upcomingMedicine
                )
            }
        }

        item {
            Text(
                text = "Today's Schedule",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (viewModel.todayMedicines.isEmpty()) {
            item { EmptyMedicinesState() }
        } else {
            items(
                items = viewModel.todayMedicines,
                key = { it.id ?: it.medicineName }
            ) { medicine ->
                MedicineItemCard(
                    medicine = medicine,
                    onClick = { medicine.id?.let { onMedicineClick(it) } }
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = userName.ifBlank { "Patient" },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black
            )
        }

        // Premium Profile Icon Placeholder
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Person, null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun UpcomingMedicineMiniCard(
    modifier: Modifier,
    upcoming: HomeViewModel.UpcomingMedicine?
) {
    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.background(gradient).fillMaxSize().padding(16.dp)) {
            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                Icon(Icons.Rounded.NotificationsActive, null, tint = Color.White, modifier = Modifier.size(24.dp))

                Column {
                    Text(
                        text = upcoming?.medReminder?.medicineName ?: "Healthy Day!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = if (upcoming != null) "Next: ${upcoming.upcomingTime}" else "No more today",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthSummaryMiniCard(modifier: Modifier, bloodGroup: String, recordCount: Int) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(Icons.Rounded.Bloodtype, null, tint = Color(0xFFEF4444), modifier = Modifier.size(28.dp))
            Column {
                Text(bloodGroup.ifBlank { "--" }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Blood Group", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
            Text("$recordCount Records", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MedicineItemCard(
    medicine: com.app.patientcareapp.feature_med_reminder.domain.model.MedReminder,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // Make the card interactive
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Medication, null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(medicine.medicineName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("Dosage: ${medicine.dosage}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
            }
            // The chevron indicates there is more to see!
            Icon(Icons.Rounded.ChevronRight, "View Details", tint = MaterialTheme.colorScheme.onSurface.copy(0.3f))
        }
    }
}

@Composable
private fun EmptyMedicinesState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Rounded.CheckCircle, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(0.1f))
        Spacer(Modifier.height(16.dp))
        Text("All medicines taken!", color = MaterialTheme.colorScheme.onSurface.copy(0.4f))
    }
}

@Composable
private fun BatteryWarningCard(onDismiss: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(0.9f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Warning, null, tint = MaterialTheme.colorScheme.error)
                Spacer(Modifier.width(8.dp))
                Text("Delayed Alarms?", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Text("Phone battery settings might delay your reminders.", style = MaterialTheme.typography.bodySmall)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Dismiss", color = MaterialTheme.colorScheme.error) }
                Button(
                    onClick = { BatteryOptimizationHelper.openBatteryOptimizationSettings(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Fix Now", fontSize = 12.sp)
                }
            }
        }
    }
}