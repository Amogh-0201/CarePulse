package com.app.patientcareapp.feature_health_records.presentation.health_records

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.patientcareapp.core.presentation.components.AppSnackbarHost
import com.app.patientcareapp.feature_health_records.domain.model.HealthRecord
import com.app.patientcareapp.feature_health_records.domain.model.RecordCategory
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

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
                    navController.navigate("add_health_record")
                }
                is HealthRecordsViewModel.UiEvent.NavigateToHealthRecordViewer -> {
                    navController.navigate("health_record_viewer/${event.healthRecordId}")
                }
                is HealthRecordsViewModel.UiEvent.ShowSnackBar -> {
                    val result = snackBarHostState.showSnackbar(message = event.message, actionLabel = "Undo")
                    if(result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(HealthRecordsScreenEvents.OnUndoDeleteHealthRecordClick)
                    }
                }
            }
        }
    }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
            MaterialTheme.colorScheme.background
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Health Records", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.onEvent(HealthRecordsScreenEvents.OnAddHealthRecordClick) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                icon = { Icon(Icons.Rounded.Add, "Add") },
                text = { Text("Upload Record", fontWeight = FontWeight.Bold) }
            )
        },
        snackbarHost = { AppSnackbarHost(hostState = snackBarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(bgGradient).padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Search Bar
                item {
                    OutlinedTextField(
                        value = viewModel.searchQuery,
                        onValueChange = { viewModel.onEvent(HealthRecordsScreenEvents.OnSearchQueryChange(it)) },
                        placeholder = { Text("Search records, hospitals, doctors...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Rounded.Search, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(0.7f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(0.5f)
                        ),
                        singleLine = true
                    )
                }

                // Category Filter Chips
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            CategoryChip(
                                label = "All",
                                isSelected = viewModel.selectedCategory == null,
                                onClick = { viewModel.onEvent(HealthRecordsScreenEvents.OnCategorySelected(null)) }
                            )
                        }
                        items(RecordCategory.entries) { category ->
                            CategoryChip(
                                label = category.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                isSelected = viewModel.selectedCategory == category,
                                onClick = { viewModel.onEvent(HealthRecordsScreenEvents.OnCategorySelected(category)) }
                            )
                        }
                    }
                }

                // List Content
                if (viewModel.isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(40.dp), Alignment.Center) {
                            CircularProgressIndicator(strokeWidth = 3.dp, modifier = Modifier.size(40.dp))
                        }
                    }
                } else if (viewModel.healthRecords.isEmpty()) {
                    item { EmptyRecordsState() }
                } else {
                    items(items = viewModel.healthRecords, key = { it.id }) { record ->
                        HealthRecordCard(
                            record = record,
                            onClick = { viewModel.onEvent(HealthRecordsScreenEvents.OnHealthRecordClick(record)) },
                            onDelete = { viewModel.onEvent(HealthRecordsScreenEvents.OnDeleteHealthRecord(record)) }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

// --- Helper UI Components ---

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = CircleShape,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.1f)),
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun HealthRecordCard(
    record: HealthRecord,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val categoryColor = getCategoryColor(record.category)
    val categoryIcon = getCategoryIcon(record.category)
    val formattedDate = remember(record.date) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(record.date))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with colored background
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(categoryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(categoryIcon, null, tint = categoryColor, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.CalendarToday, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Spacer(Modifier.width(4.dp))
                    Text(formattedDate, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!record.hospitalName.isNullOrBlank()) {
                        Icon(Icons.Rounded.Business, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(Modifier.width(4.dp))
                        Text(record.hospitalName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }

                    if (!record.doctorName.isNullOrBlank()) {
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Rounded.Person, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(Modifier.width(4.dp))
                        Text(record.doctorName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.background(MaterialTheme.colorScheme.error.copy(0.05f), CircleShape).size(32.dp)
            ) {
                Icon(Icons.Rounded.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun EmptyRecordsState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Rounded.HistoryEdu, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary.copy(0.1f))
        Spacer(Modifier.height(16.dp))
        Text("No health records found", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontWeight = FontWeight.Medium)
    }
}

// Logic Helpers for UI
fun getCategoryColor(category: RecordCategory) = when(category) {
    RecordCategory.PRESCRIPTION -> Color(0xFF3B82F6)
    RecordCategory.REPORT -> Color(0xFF10B981)
    RecordCategory.SCAN -> Color(0xFF8B5CF6)
    RecordCategory.X_RAY -> Color(0xFFF59E0B)
    RecordCategory.DISCHARGE_SUMMARY -> Color(0xFF6366F1)
    RecordCategory.OTHER -> Color(0xFF64748B)
}

fun getCategoryIcon(category: RecordCategory) = when(category) {
    RecordCategory.PRESCRIPTION -> Icons.Rounded.Description
    RecordCategory.REPORT -> Icons.Rounded.Assessment
    RecordCategory.SCAN -> Icons.Rounded.DocumentScanner
    RecordCategory.X_RAY -> Icons.Rounded.Visibility
    RecordCategory.DISCHARGE_SUMMARY -> Icons.Rounded.AssignmentTurnedIn
    RecordCategory.OTHER -> Icons.Rounded.Folder
}