package com.app.patientcareapp.feature_med_reminder.presentation.show_med_reminders

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
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.patientcareapp.core.presentation.components.AddMedReminderFab
import com.app.patientcareapp.core.presentation.components.AppSnackbarHost
import com.app.patientcareapp.feature_med_reminder.domain.model.MedReminder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MedReminderScreen(
    viewModel: MedReminderViewModel = hiltViewModel(),
    navController: NavController
) {
    val medReminders by viewModel.medReminders.collectAsState(initial = emptyList())
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is MedReminderViewModel.UiEvent.NavigateToAddMedReminder -> {
                    navController.navigate("add_edit_med_reminder")
                }
                is MedReminderViewModel.UiEvent.ShowSnackBar -> {
                    scope.launch {
                        val result = snackBarHostState.showSnackbar(
                            message = event.message,
                            actionLabel = event.action,
                            duration = SnackbarDuration.Short
                        )
                        if(result == SnackbarResult.ActionPerformed) {
                            viewModel.onEvent(MedReminderScreenEvents.OnUndoDeleteMedReminderClick)
                        }
                    }
                }
                is MedReminderViewModel.UiEvent.NavigateToEditMedReminder -> {
                    navController.navigate("add_edit_med_reminder?id=${event.id}")
                }
            }
        }
    }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
            MaterialTheme.colorScheme.background
        ),
        startY = 0f,
        endY = 1000f
    )

    Scaffold(
        snackbarHost = { AppSnackbarHost(hostState = snackBarHostState) },
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            AddMedReminderFab(
                onClick = {
                    viewModel.onEvent(
                        MedReminderScreenEvents.OnAddMedReminderClick
                    )
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(bgGradient).padding(padding)) {
            if (medReminders.isEmpty()) {
                EmptyRemindersState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Medications",
                            modifier = Modifier.padding(start = 12.dp),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black
                        )
                    }
                    items(medReminders, key = { it.id ?: it.medicineName }) { reminder ->
                        Box(
                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(500),
                                fadeOutSpec = tween(500),
                                placementSpec = tween(500)
                            )
                        ) {
                            EnhancedMedReminderItem(
                                reminder = reminder,
                                onClick = { reminder.id?.let { viewModel.onEvent(MedReminderScreenEvents.OnMedReminderClick(it)) } },
                                onDeleteClick = { reminder.id?.let { viewModel.onEvent(MedReminderScreenEvents.OnDeleteMedReminderClick(it)) } }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EnhancedMedReminderItem(
    reminder: MedReminder,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // Logic to check if the end date has passed
    val isExpired = reminder.endDate?.let { it < System.currentTimeMillis() } ?: false

    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                // Icon background changes based on status
                val iconBgColor = when {
                    isExpired -> MaterialTheme.colorScheme.onSurface.copy(0.05f)
                    !reminder.isActive -> MaterialTheme.colorScheme.error.copy(0.1f)
                    else -> MaterialTheme.colorScheme.primary.copy(0.1f)
                }

                Box(
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Medication, null,
                        tint = if (isExpired || !reminder.isActive) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reminder.medicineName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isExpired) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                    )
                    Text(reminder.dosage, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Surface(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.07f),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteOutline,
                                contentDescription = "Delete health record",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Status Badges
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatusBadge(icon = Icons.Rounded.Event, text = formatRepeat(reminder.repeatType))

                when {
                    isExpired -> {
                        StatusBadge(
                            icon = Icons.Rounded.History,
                            text = "Inactive (Ended)",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    !reminder.isActive -> {
                        StatusBadge(
                            icon = Icons.Rounded.PauseCircle,
                            text = "Paused",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {
                        StatusBadge(
                            icon = Icons.Rounded.CheckCircle,
                            text = "Active",
                            color = Color(0xFF10B981) // Success Green
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date Range
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.DateRange, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Spacer(Modifier.width(8.dp))
                val dateText = remember(reminder.startDate, reminder.endDate) {
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val start = sdf.format(Date(reminder.startDate))
                    val end = reminder.endDate?.let { sdf.format(Date(it)) }
                    if (end != null) "$start - $end" else "$start (Ongoing)"
                }
                Text(dateText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }

            if (!reminder.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.AutoMirrored.Rounded.Notes, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Spacer(Modifier.width(8.dp))
                    Text(reminder.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                reminder.times.forEach { time ->
                    Surface(
                        shape = CircleShape,
                        color = if (isExpired) Color.LightGray.copy(0.2f) else MaterialTheme.colorScheme.primary.copy(0.05f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(0.1f))
                    ) {
                        Text(
                            text = time,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isExpired) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun StatusBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color = MaterialTheme.colorScheme.primary) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(color.copy(0.1f), CircleShape).padding(horizontal = 10.dp, vertical = 4.dp)) {
        Icon(icon, null, modifier = Modifier.size(14.dp), tint = color)
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.Bold)
    }
}

private fun formatRepeat(type: String) = when(type) {
    "DAILY" -> "Daily"
    "EVERY_2_DAYS" -> "Alternate Days"
    "EVERY_3_DAYS" -> "Every 3 Days"
    "WEEKLY" -> "Weekly"
    else -> type
}

@Composable
fun EmptyRemindersState() {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.AutoMirrored.Rounded.PlaylistAdd, null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary.copy(0.1f))
        Spacer(Modifier.height(16.dp))
        Text("No reminders set", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface.copy(0.4f))
    }
}