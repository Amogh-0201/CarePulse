package com.app.patientcareapp.feature_med_reminder.presentation.show_med_reminders

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
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

                if (medReminders.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxHeight(0.8f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyRemindersState()
                        }
                    }
                } else {
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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = if (isSystemInDarkTheme()) 0.5f else 0.5f))
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
                        color = if (isExpired) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )

                    Spacer(Modifier.height(5.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)
                    ) {
                        Text(
                            text = reminder.dosage,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary.copy(
                                alpha = if (isExpired) 0.45f else 0.85f
                            ),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
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

            // --------------------------------------------------
            // Status
            // --------------------------------------------------

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(
                    icon = Icons.Rounded.EventRepeat,
                    text = formatRepeat(reminder.repeatType)
                )

                when {
                    isExpired -> {
                        StatusBadge(
                            icon = Icons.Rounded.History,
                            text = "Ended",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
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
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // --------------------------------------------------
            // Treatment Period
            // --------------------------------------------------

            ReminderSectionLabel(
                icon = Icons.Rounded.DateRange,
                text = "Treatment period"
            )

            Spacer(modifier = Modifier.height(8.dp))

            val dateText = remember(reminder.startDate, reminder.endDate) {
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

                val start = sdf.format(Date(reminder.startDate))
                val end = reminder.endDate?.let { sdf.format(Date(it)) }

                start to end
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primary.copy(
                    alpha = if (isExpired) 0.04f else 0.07f
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 11.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Event,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(
                            alpha = if (isExpired) 0.45f else 0.9f
                        )
                    )

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = dateText.first,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = if (isExpired) 0.5f else 0.8f
                        )
                    )

                    if (dateText.second != null) {
                        Text(
                            text = "  →  ",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = dateText.second!!,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = if (isExpired) 0.5f else 0.8f
                            )
                        )
                    } else {
                        Spacer(Modifier.width(6.dp))

                        Text(
                            text = "Ongoing",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // --------------------------------------------------
            // Reminder Times
            // --------------------------------------------------

            ReminderSectionLabel(
                icon = Icons.Rounded.Alarm,
                text = "Reminder times"
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                reminder.times.forEach { time ->

                    Surface(
                        shape = CircleShape,
                        color = if (isExpired) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                        } else {
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
                        },
                        border = BorderStroke(
                            1.dp,
                            if (isExpired) {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
                            } else {
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.28f)
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isExpired) {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                                } else {
                                    MaterialTheme.colorScheme.secondary
                                }
                            )

                            Text(
                                text = time,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isExpired) {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                } else {
                                    MaterialTheme.colorScheme.secondary
                                }
                            )
                        }
                    }
                }
            }

            // --------------------------------------------------
            // Notes
            // --------------------------------------------------

            if (!reminder.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(18.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.06f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Notes,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )

                        Spacer(Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Notes / Instructions",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            Spacer(Modifier.height(2.dp))

                            Text(
                                text = reminder.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderSectionLabel(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
        )
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.AutoMirrored.Rounded.PlaylistAdd, null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary.copy(0.1f))
        Spacer(Modifier.height(16.dp))
        Text("No reminders set", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface.copy(0.4f))
    }
}