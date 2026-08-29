package com.app.patientcareapp.feature_home.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.app.patientcareapp.feature_med_reminder.domain.model.MedReminder
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
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.app.patientcareapp.core.util.Screen
import com.app.patientcareapp.feature_med_reminder.data.alarm.MedicineAlarmScheduler

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
    onMedicineClick: (Int) -> Unit,
    onRecordsCountClick: () -> Unit
) {
    val context = LocalContext.current
    val isAlarmWarningDismissed by viewModel.isAlarmWarningDismissed.collectAsState()
    var isAlarmPermissionMissing by remember { mutableStateOf(false) }

    // Logic to detect loading: if userName is blank and no medicines yet, we are likely loading
    val isLoading = viewModel.userName.isBlank() && viewModel.todayMedicines.isEmpty()

    LaunchedEffect(Unit) {
        val scheduler = MedicineAlarmScheduler(context)
        isAlarmPermissionMissing = !scheduler.hasExactAlarmPermission()
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
                    isAlarmPermissionMissing = isAlarmPermissionMissing,
                    isAlarmWarningDismissed = isAlarmWarningDismissed,
                    onMedicineClick = onMedicineClick,
                    onProfileClick = {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onRecordsCountClick = onRecordsCountClick
                )
            }
        }
    }
}

@Composable
private fun HomeScreenContent(
    viewModel: HomeViewModel,
    paddingValues: PaddingValues,
    isAlarmPermissionMissing: Boolean,
    isAlarmWarningDismissed: Boolean,
    onMedicineClick: (Int) -> Unit,
    onProfileClick: () -> Unit,
    onRecordsCountClick: () -> Unit
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
        item { HeaderSection(userName = viewModel.userName, onProfileClick = onProfileClick) }

        if (isAlarmPermissionMissing && !isAlarmWarningDismissed) {
            item { AlarmPermissionWarningCard(onDismiss = { viewModel.dismissAlarmWarning() }) }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HealthSummaryMiniCard(
                    modifier = Modifier.weight(1f),
                    bloodGroup = viewModel.bloodGroup,
                    recordCount = viewModel.totalHealthRecords,
                    onRecordsCountClick = onRecordsCountClick
                )

                UpcomingMedicineMiniCard(
                    modifier = Modifier.weight(1.15f),
                    upcoming = viewModel.upcomingMedicine
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Today's Schedule",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = "Stay on track with your medications",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                if (viewModel.todayMedicines.isNotEmpty()) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = "${viewModel.todayMedicines.size}",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
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
private fun HeaderSection(
    userName: String,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome,",
                style = MaterialTheme.typography.labelLarge,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = userName.ifBlank { "User" },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black
            )
        }

        // Premium Profile Icon Placeholder
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                .clickable(onClick = onProfileClick),
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
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.12f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.NotificationsActive,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "Next Medication",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                if (upcoming != null) {

                    Text(
                        text = upcoming.medReminder.medicineName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        maxLines = 1
                    )

                    Text(
                        text = upcoming.medReminder.dosage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.78f),
                        maxLines = 1
                    )

                    Spacer(Modifier.weight(1f))

                    // Next dose pill
                    Surface(
                        shape = RoundedCornerShape(13.dp),
                        color = Color.White.copy(alpha = 0.16f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 7.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )

                            Spacer(Modifier.width(6.dp))

                            Text(
                                text = "Next dose",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.75f)
                            )

                            Spacer(Modifier.weight(1f))

                            Text(
                                text = upcoming.upcomingTime,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }

                } else {

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = "Healthy Day!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Spacer(Modifier.height(3.dp))

                    Text(
                        text = "No more medicines scheduled today",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.78f)
                    )

                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HealthSummaryMiniCard(
    modifier: Modifier,
    bloodGroup: String,
    recordCount: Int,
    onRecordsCountClick: () -> Unit
) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEF4444).copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Bloodtype,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Health Profile",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }

            Spacer(Modifier.height(8.dp))

            // Blood group
            Column {
                Text(
                    text = bloodGroup.ifBlank { "--" },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Blood Group",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(Modifier.weight(1f))

            // Records action
            Surface(
                onClick = onRecordsCountClick,
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Description,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = "$recordCount Records",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.weight(1f))

                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun MedicineItemCard(
    medicine: MedReminder,
    onClick: () -> Unit
) {
    val sortedTimes = remember(medicine.times) {
        medicine.times.sorted()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.30f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            // ---------------------------------------
            // Medicine header
            // ---------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.09f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Medication,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = medicine.medicineName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = medicine.dosage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.55f
                        ),
                        maxLines = 1
                    )
                }

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = "View medication",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.35f
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
            )

            Spacer(Modifier.height(12.dp))

            // ---------------------------------------
            // Today's reminder times
            // ---------------------------------------

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = "Today's doses",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.60f
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sortedTimes.forEach { t ->

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondary.copy(
                            alpha = 0.09f
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.secondary.copy(
                                alpha = 0.22f
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )

                            Text(
                                text = t,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyMedicinesState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Rounded.CheckCircle, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(0.2f))
        Spacer(Modifier.height(16.dp))
        Text("All medicines taken!", color = MaterialTheme.colorScheme.onSurface.copy(0.4f))
    }
}

@Composable
private fun AlarmPermissionWarningCard(onDismiss: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(0.9f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Warning, null, tint = MaterialTheme.colorScheme.error)
                Spacer(Modifier.width(8.dp))
                Text("Exact Reminders Disabled", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Text("Without the 'Alarms & Reminders' permission, your medicine notifications might be delayed by the system.", style = MaterialTheme.typography.bodySmall)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Dismiss", color = MaterialTheme.colorScheme.error) }
                Button(
                    onClick = {
                        val scheduler = MedicineAlarmScheduler(context)
                        scheduler.openExactAlarmSettings()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Fix Now", fontSize = 12.sp)
                }
            }
        }
    }
}
