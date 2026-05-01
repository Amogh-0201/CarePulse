package com.app.patientcareapp.feature_med_reminder.presentation.show_med_reminders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.patientcareapp.feature_med_reminder.domain.model.MedReminder

@Composable
fun MedReminderScreen(
    viewModel: MedReminderViewModel = hiltViewModel(),
    navController: NavController
) {

    val medReminders = viewModel.medReminders.collectAsState(initial = emptyList())
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is MedReminderViewModel.UiEvent.NavigateToAddMedReminder -> {
                    navController.navigate("add_edit_med_reminder")
                }
                is MedReminderViewModel.UiEvent.ShowSnackBar -> {
                    val result = snackBarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                    if( result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(MedReminderScreenEvents.OnUndoDeleteMedReminderClick)
                    }
                }
                is MedReminderViewModel.UiEvent.NavigateToEditMedReminder -> {
                    navController.navigate("add_edit_med_reminder?id=${event.id}")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(MedReminderScreenEvents.OnAddMedReminderClick)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Med Reminder"
                )
            }
        }
    ) {
        if (medReminders.value.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No reminders yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                items(medReminders.value) { reminder ->
                    MedReminderItem(
                        reminder = reminder,
                        onClick = {
                            viewModel.onEvent(
                                MedReminderScreenEvents.OnMedReminderClick(reminder.id!!)
                            )
                        },
                        onDeleteClick = {
                            viewModel.onEvent(
                                MedReminderScreenEvents.OnDeleteMedReminderClick(reminder.id!!)
                            )
                        }
                    )
                }
            }
        }
    }
}


//helper function
@Composable
fun MedReminderItem(
    reminder: MedReminder,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable{ onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = reminder.medicineName)
                    Text(text = reminder.dosage)
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Times
            FlowRow {
                reminder.times.forEach { time ->
                    AssistChip(
                        onClick = {},
                        label = { Text(time) }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }
    }
}