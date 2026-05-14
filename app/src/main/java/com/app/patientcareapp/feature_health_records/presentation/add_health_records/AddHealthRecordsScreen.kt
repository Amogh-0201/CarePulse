package com.app.patientcareapp.feature_health_records.presentation.add_health_records

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.patientcareapp.feature_health_records.domain.model.FileType
import com.app.patientcareapp.feature_health_records.domain.model.RecordCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHealthRecordsScreen(
    navController: NavController,
    viewModel: AddHealthRecordsViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    var expanded by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {uri ->

        uri?.let {

            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            val mimeType = context.contentResolver.getType(it)

            val fileType = if (
                mimeType?.startsWith("image") == true
            ) {
                FileType.IMAGE
            } else {
                FileType.PDF
            }

            viewModel.onEvent(
                AddHealthRecordsEvents.OnFilePicked(
                    uri = it.toString(),
                    fileType = fileType
                )
            )
        }
    }

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is AddHealthRecordsViewModel.UiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(event.message)
                }
                is AddHealthRecordsViewModel.UiEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Add Health Record")
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        modifier = Modifier.fillMaxSize()
    ) {innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = viewModel.title,
                onValueChange = {
                    viewModel.onEvent(AddHealthRecordsEvents.OnTitleChange(it))
                },
                label = {
                    Text(text = "Title")
                },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                OutlinedTextField(
                    value = viewModel.category.name,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(text = "Category")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    RecordCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(text = category.name) },
                            onClick = {
                                viewModel.onEvent(AddHealthRecordsEvents.OnCategoryChange(category))
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = viewModel.hospitalName,
                onValueChange = {
                    viewModel.onEvent(
                        AddHealthRecordsEvents.OnHospitalNameChange(it)
                    )
                },
                label = {
                    Text(text = "Hospital Name")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.doctorName,
                onValueChange = {
                    viewModel.onEvent(
                        AddHealthRecordsEvents.OnDoctorNameChange(it)
                    )
                },
                label = {
                    Text(text = "Doctor Name")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.notes,
                onValueChange = {
                    viewModel.onEvent(
                        AddHealthRecordsEvents.OnNotesChange(it)
                    )
                },
                label = {
                    Text(text = "Notes")
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    launcher.launch(
                        arrayOf(
                            "image/*",
                            "application/pdf"
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Select PDF / Image")
            }

            if(viewModel.fileUri.isNotBlank()) {
                Text(text = "File Selected")
            }

            Button(
                onClick = {
                    viewModel.onEvent(AddHealthRecordsEvents.OnSaveHealthRecord)
                },
                enabled = viewModel.healthRecordInfoValid(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Record")
            }

        }
    }

}