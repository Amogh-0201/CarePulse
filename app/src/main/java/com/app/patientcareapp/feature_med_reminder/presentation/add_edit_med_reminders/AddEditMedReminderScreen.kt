package com.app.patientcareapp.feature_med_reminder.presentation.add_edit_med_reminders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.patientcareapp.core.util.Screen
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMedReminderScreen(
    navController: NavController,
    viewModel: AddEditMedReminderViewModel = hiltViewModel(),
    modifier: Modifier
) {

    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(true) {
        viewModel.uiEvent.collect {event ->
            when(event) {
                is AddEditMedReminderViewModel.UiEvent.ShowError -> {
                    snackBarHostState.showSnackbar(event.message!!)
                }
                is AddEditMedReminderViewModel.UiEvent.SaveSuccess -> {
                    navController.navigate(Screen.MedReminder.route) {
                        popUpTo("Add_Edit_MedReminder") { inclusive = true}
                    }
                }
            }
        }
    }


    val showTimePicker = remember { mutableStateOf(false) }
    val showStartDatePicker = remember { mutableStateOf(false)}
    val showEndDatePicker = remember { mutableStateOf(false)}

    val repeatOptions = listOf(
        "Daily" to "DAILY",
        "Alternate Days" to "EVERY_2_DAYS",
        "Every 3 Days" to "EVERY_3_DAYS",
        "Weekly" to "WEEKLY"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = viewModel.heading!!)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Basic Info")

            Spacer(modifier = Modifier.height(8.dp))

            MedTextField(
                value = viewModel.name,
                label = "Medicine Name*",
                onValueChange = {
                    viewModel.onEvent(AddEditMedReminderEvents.OnNameChange(it))
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            MedTextField(
                value = viewModel.dosage,
                label = "Dosage*",
                onValueChange = {
                    viewModel.onEvent(AddEditMedReminderEvents.OnDosageChange(it))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Reminder Times*")

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                showTimePicker.value = true
            }) {
                Text("Add Time")
            }

            FlowRow {
                viewModel.times.forEach {
                    AssistChip(
                        onClick = {},
                        label = { Text(it) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete",
                                modifier = Modifier.clickable(
                                    onClick = {
                                        viewModel.onEvent(AddEditMedReminderEvents.OnDeleteTime(time = it))
                                    }
                                )
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Start Date*")
            Button(
                onClick = {
                    showStartDatePicker.value = true
                }
            ) {
                Text(
                    text = viewModel.startDate?.let {
                        formatDate(it)
                    }?: "Select Start Date"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("End Date")
            Button(
                onClick = {
                    showEndDatePicker.value = true
                }
            ) {
                Text(
                    text = viewModel.endDate?.let {
                        formatDate(it)
                    }?: "Select End Date"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Repeat*")

            Spacer(modifier = Modifier.height(8.dp))

            repeatOptions.forEach { (label, value) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = viewModel.repeatType == value,
                        onClick = {
                            viewModel.onEvent(AddEditMedReminderEvents.OnRepeatTypeChange(value))
                        }
                    )
                    Text(text = label)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Reminder Active")
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = viewModel.isActive,
                    onCheckedChange = {
                        viewModel.onEvent(AddEditMedReminderEvents.OnIsActiveChange(it))
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = viewModel.notes,
                onValueChange = {
                    viewModel.onEvent(AddEditMedReminderEvents.OnNotesChange(it))
                },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.onEvent(AddEditMedReminderEvents.OnSaveButtonClick)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Reminder")
            }

        }

        //show time picker
        if(showTimePicker.value) {
            val timePickerState = rememberTimePickerState(is24Hour = true)

            AlertDialog(
                onDismissRequest = { showTimePicker.value = false},
                confirmButton = {
                    TextButton(
                        onClick = {
                            val hour = timePickerState.hour
                            val minute = timePickerState.minute
                            val formattedTime = String.format(
                                java.util.Locale.getDefault(),
                                "%02d:%02d",
                                hour, minute
                            )
                            viewModel.onEvent(AddEditMedReminderEvents.OnTimesChange(
                                viewModel.times + formattedTime
                            ))
                            showTimePicker.value = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {showTimePicker.value = false}
                    ) {
                        Text("Cancel")
                    }
                },
                text = {
                    TimePicker(state = timePickerState)
                }
            )
        }

        //show start date picker
        if(showStartDatePicker.value) {
            val datePickerState = rememberDatePickerState()

            DatePickerDialog(
                onDismissRequest = { showStartDatePicker.value = false},
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                viewModel.onEvent(AddEditMedReminderEvents.OnStartDateChange(it))
                            }
                            showStartDatePicker.value = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showStartDatePicker.value = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        //show end date picker
        if(showEndDatePicker.value) {
            val datePickerState = rememberDatePickerState()

            DatePickerDialog(
                onDismissRequest = { showEndDatePicker.value = false},
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                viewModel.onEvent(AddEditMedReminderEvents.OnEndDateChange(it))
                            }
                            showEndDatePicker.value = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showEndDatePicker.value = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

//helper functions
@Composable
fun MedTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
}

fun formatDate(millis: Long): String {
    val formatter = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
    return formatter.format(java.util.Date(millis))
}