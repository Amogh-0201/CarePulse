package com.app.patientcareapp.feature_med_reminder.presentation.add_edit_med_reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.patientcareapp.core.util.Screen
import com.app.patientcareapp.ui.theme.PrimaryBlue
import com.app.patientcareapp.ui.theme.SecondaryTeal

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditMedReminderScreen(
    navController: NavController,
    viewModel: AddEditMedReminderViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AddEditMedReminderViewModel.UiEvent.ShowError -> {
                    snackBarHostState.showSnackbar(event.message ?: "An error occurred")
                }
                is AddEditMedReminderViewModel.UiEvent.SaveSuccess -> {
                    navController.navigate(Screen.MedReminder.route) {
                        popUpTo("Add_Edit_MedReminder") { inclusive = true }
                    }
                }
            }
        }
    }

    val showTimePicker = remember { mutableStateOf(false) }
    val showStartDatePicker = remember { mutableStateOf(false) }
    val showEndDatePicker = remember { mutableStateOf(false) }

    val repeatOptions = listOf(
        "Daily" to "DAILY",
        "Alternate Days" to "EVERY_2_DAYS",
        "Every 3 Days" to "EVERY_3_DAYS",
        "Weekly" to "WEEKLY"
    )

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
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(viewModel.heading ?: "Med Reminder", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // Section 1: Basic Info
                PremiumFormCard(title = "Medicine Details", icon = Icons.Rounded.Medication) {
                    OutlinedTextField(
                        value = viewModel.name,
                        onValueChange = { viewModel.onEvent(AddEditMedReminderEvents.OnNameChange(it)) },
                        label = { Text("Medicine Name*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Rounded.Label, null, tint = MaterialTheme.colorScheme.primary) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = viewModel.dosage,
                        onValueChange = { viewModel.onEvent(AddEditMedReminderEvents.OnDosageChange(it)) },
                        label = { Text("Dosage (e.g., 1 tablet)*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Rounded.Opacity, null, tint = MaterialTheme.colorScheme.primary) }
                    )
                }

                // Section 2: Timing
                PremiumFormCard(title = "Schedule", icon = Icons.Rounded.CalendarToday) {
                    SelectionRow(
                        label = "Start Date*",
                        value = viewModel.startDate?.let { formatDate(it) } ?: "Select Start Date",
                        icon = Icons.Rounded.EventAvailable,
                        onClick = { showStartDatePicker.value = true }
                    )

                    SelectionRow(
                        label = "End Date (Optional)",
                        value = viewModel.endDate?.let { formatDate(it) } ?: "Set duration",
                        icon = Icons.Rounded.EventBusy,
                        onClick = { showEndDatePicker.value = true }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Reminder Times*", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)

                    FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.times.forEach { time ->
                            AssistChip(
                                onClick = { },
                                label = { Text(time, fontWeight = FontWeight.Bold) },
                                shape = CircleShape,
                                colors = AssistChipDefaults.assistChipColors(
                                    labelColor = MaterialTheme.colorScheme.primary,
                                    containerColor = MaterialTheme.colorScheme.primary.copy(0.1f)
                                ),
                                trailingIcon = {
                                    Icon(
                                        Icons.Rounded.Cancel, "Delete",
                                        modifier = Modifier.size(18.dp).clickable {
                                            viewModel.onEvent(AddEditMedReminderEvents.OnDeleteTime(time))
                                        }
                                    )
                                }
                            )
                        }
                        // Add Time Button
                        AssistChip(
                            onClick = { showTimePicker.value = true },
                            label = { Text("Add Time") },
                            shape = CircleShape,
                            leadingIcon = { Icon(Icons.Rounded.Add, null, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }

                // Section 3: Repeat Options
                PremiumFormCard(title = "Recurrence", icon = Icons.Rounded.Update) {
                    repeatOptions.forEach { (label, value) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.onEvent(AddEditMedReminderEvents.OnRepeatTypeChange(value)) }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = viewModel.repeatType == value,
                                onClick = { viewModel.onEvent(AddEditMedReminderEvents.OnRepeatTypeChange(value)) }
                            )
                            Text(text = label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f) // Correct way to apply alpha
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Active Status", fontWeight = FontWeight.Bold)
                            Text("Receive notifications for this medicine", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Switch(
                            checked = viewModel.isActive,
                            onCheckedChange = { viewModel.onEvent(AddEditMedReminderEvents.OnIsActiveChange(it)) }
                        )
                    }
                }

                // Section 4: Notes
                OutlinedTextField(
                    value = viewModel.notes,
                    onValueChange = { viewModel.onEvent(AddEditMedReminderEvents.OnNotesChange(it)) },
                    label = { Text("Notes & Instructions (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.onEvent(AddEditMedReminderEvents.OnSaveButtonClick) },
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    shape = RoundedCornerShape(18.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(Icons.Rounded.Save, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save Reminder", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // --- Existing Dialog Logic (Maintained) ---
        if (showTimePicker.value) {
            val timePickerState = rememberTimePickerState(is24Hour = true)
            AlertDialog(
                onDismissRequest = { showTimePicker.value = false },
                confirmButton = {
                    TextButton(onClick = {
                        val formattedTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                        viewModel.onEvent(AddEditMedReminderEvents.OnTimesChange(viewModel.times + formattedTime))
                        showTimePicker.value = false
                    }) { Text("Confirm") }
                },
                dismissButton = { TextButton(onClick = { showTimePicker.value = false }) { Text("Cancel") } },
                text = { TimePicker(state = timePickerState) }
            )
        }

        if (showStartDatePicker.value) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showStartDatePicker.value = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { viewModel.onEvent(AddEditMedReminderEvents.OnStartDateChange(it)) }
                        showStartDatePicker.value = false
                    }) { Text("OK") }
                },
                dismissButton = { TextButton(onClick = { showStartDatePicker.value = false }) { Text("Cancel") } }
            ) { DatePicker(state = datePickerState) }
        }

        if (showEndDatePicker.value) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker.value = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { viewModel.onEvent(AddEditMedReminderEvents.OnEndDateChange(it)) }
                        showEndDatePicker.value = false
                    }) { Text("OK") }
                },
                dismissButton = { TextButton(onClick = { showEndDatePicker.value = false }) { Text("Cancel") } }
            ) { DatePicker(state = datePickerState) }
        }
    }
}

@Composable
fun PremiumFormCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun SelectionRow(label: String, value: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.weight(1f))
        Icon(Icons.Rounded.ChevronRight, null, tint = Color.LightGray)
    }
}

fun formatDate(millis: Long): String {
    val formatter = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
    return formatter.format(java.util.Date(millis))
}