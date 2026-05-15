package com.app.patientcareapp.feature_health_records.presentation.health_records

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.patientcareapp.feature_health_records.domain.model.RecordCategory
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordsScreen(
    navController: NavController,
    viewModel: HealthRecordsViewModel = hiltViewModel()
) {

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.uiEvent.collectLatest { event ->
            when(event) {
                is HealthRecordsViewModel.UiEvent.NavigateToAddHealthRecords -> {
                    navController.navigate(
                        "add_health_record"
                    )
                }
                is HealthRecordsViewModel.UiEvent.NavigateToHealthRecordViewer -> {
                    navController.navigate(
                        "health_record_viewer/${event.healthRecordId}"
                    )
                }
                is HealthRecordsViewModel.UiEvent.ShowSnackBar -> {
                    val result = snackBarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "Undo"
                    )
                    if(result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(HealthRecordsScreenEvents.OnUndoDeleteHealthRecordClick)
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Health Records")
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(HealthRecordsScreenEvents.OnAddHealthRecordClick)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Health Record"
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp), // Padding moved here
            horizontalAlignment = Alignment.Start
        ) {

            item {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = viewModel.searchQuery,
                    onValueChange = { viewModel.onEvent(HealthRecordsScreenEvents.OnSearchQueryChange(it)) },
                    label = { Text("Search Records") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        AssistChip(
                            onClick = {
                                viewModel.onEvent(
                                    HealthRecordsScreenEvents.OnCategorySelected(null)
                                )
                            },
                            label = {
                                Text("All")
                            }
                        )
                    }

                    items(RecordCategory.entries) { category ->
                        AssistChip(
                            onClick = {
                                viewModel.onEvent(
                                    HealthRecordsScreenEvents.OnCategorySelected(category)
                                )
                            },
                            label = {
                                Text(
                                    category.name
                                        .replace("_", " ")
                                        .lowercase()
                                        .replaceFirstChar { it.uppercase() }
                                )
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.padding(8.dp))
            }

            if(viewModel.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            items(viewModel.healthRecords) {record ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            viewModel.onEvent(HealthRecordsScreenEvents.OnHealthRecordClick(record))
                        }
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = record.title
                                )
                                Spacer(modifier = Modifier.padding(2.dp))
                                Text(
                                    text = record.category.name
                                )
                                if(record.hospitalName != null) {
                                    Spacer(modifier = Modifier.padding(2.dp))
                                    Text(
                                        text = record.hospitalName,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    viewModel.onEvent(
                                        HealthRecordsScreenEvents.OnDeleteHealthRecord(record)
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Record"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

