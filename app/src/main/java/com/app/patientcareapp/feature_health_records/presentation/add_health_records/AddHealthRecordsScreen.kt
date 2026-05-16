package com.app.patientcareapp.feature_health_records.presentation.add_health_records

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.patientcareapp.feature_health_records.domain.model.FileType
import com.app.patientcareapp.feature_health_records.domain.model.RecordCategory
import com.app.patientcareapp.ui.theme.PrimaryBlue
import com.app.patientcareapp.ui.theme.SecondaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHealthRecordsScreen(
    navController: NavController,
    viewModel: AddHealthRecordsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var categoryExpanded by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    // Logic: File Picker Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val mimeType = context.contentResolver.getType(it)
            val fileType = if (mimeType?.startsWith("image") == true) FileType.IMAGE else FileType.PDF
            viewModel.onEvent(AddHealthRecordsEvents.OnFilePicked(it.toString(), fileType))
        }
    }

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is AddHealthRecordsViewModel.UiEvent.ShowSnackBar -> snackBarHostState.showSnackbar(event.message)
                is AddHealthRecordsViewModel.UiEvent.NavigateBack -> navController.popBackStack()
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
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Upload Record", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(bgGradient).padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // Section 1: Basic Information
                HealthFormCard(title = "General Information", icon = Icons.Rounded.Description) {
                    OutlinedTextField(
                        value = viewModel.title,
                        onValueChange = { viewModel.onEvent(AddHealthRecordsEvents.OnTitleChange(it)) },
                        label = { Text("Record Title (e.g., Annual Checkup)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Rounded.Edit, null, tint = MaterialTheme.colorScheme.primary) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = viewModel.category.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Record Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            leadingIcon = { Icon(Icons.Rounded.Category, null, tint = MaterialTheme.colorScheme.primary) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
                            shape = RoundedCornerShape(16.dp)
                        )
                        ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                            RecordCategory.entries.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                                    onClick = {
                                        viewModel.onEvent(AddHealthRecordsEvents.OnCategoryChange(category))
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Section 2: Hospital Details
                HealthFormCard(title = "Medical Provider", icon = Icons.Rounded.Business) {
                    OutlinedTextField(
                        value = viewModel.hospitalName,
                        onValueChange = { viewModel.onEvent(AddHealthRecordsEvents.OnHospitalNameChange(it)) },
                        label = { Text("Hospital / Clinic Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Rounded.LocalHospital, null, tint = MaterialTheme.colorScheme.primary) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = viewModel.doctorName,
                        onValueChange = { viewModel.onEvent(AddHealthRecordsEvents.OnDoctorNameChange(it)) },
                        label = { Text("Doctor's Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Rounded.Person, null, tint = MaterialTheme.colorScheme.primary) }
                    )
                }

                // Section 3: File Upload
                HealthFormCard(title = "Attachment", icon = Icons.Rounded.CloudUpload) {
                    val isFileSelected = viewModel.fileUri.isNotBlank()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isFileSelected) Color(0xFF10B981).copy(0.05f) else MaterialTheme.colorScheme.primary.copy(0.05f))
                            .clickable { launcher.launch(arrayOf("image/*", "application/pdf")) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (isFileSelected) Icons.Rounded.CheckCircle else Icons.Rounded.AddPhotoAlternate,
                                contentDescription = null,
                                tint = if (isFileSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = if (isFileSelected) "File successfully selected" else "Tap to upload PDF or Image",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isFileSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            if (isFileSelected) {
                                Text("Tap again to change file", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        }
                    }
                }

                // Section 4: Notes
                OutlinedTextField(
                    value = viewModel.notes,
                    onValueChange = { viewModel.onEvent(AddHealthRecordsEvents.OnNotesChange(it)) },
                    label = { Text("Additional Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    minLines = 3
                )

                Button(
                    onClick = { viewModel.onEvent(AddHealthRecordsEvents.OnSaveHealthRecord) },
                    enabled = viewModel.healthRecordInfoValid(),
                    modifier = Modifier.fillMaxWidth().height(58.dp).padding(bottom = 12.dp),
                    shape = RoundedCornerShape(18.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(Icons.Rounded.Save, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save Health Record", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun HealthFormCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.5.dp)
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