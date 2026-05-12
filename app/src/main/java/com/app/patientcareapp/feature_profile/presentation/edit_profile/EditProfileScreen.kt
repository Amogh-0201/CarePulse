package com.app.patientcareapp.feature_profile.presentation.edit_profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel()
) {

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(key1 = true) {

        viewModel.uiEvent.collect { event ->

            when(event) {

                is EditProfileViewModel.UiEvent.NavigateBackToProfileScreen -> {
                    navController.popBackStack()
                }

                is EditProfileViewModel.UiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },

        topBar = {

            CenterAlignedTopAppBar(
                title = {
                    Text("Edit Profile")
                },

                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = viewModel.name,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = buildString {
                            append(viewModel.gender?.displayName ?: "NA")
                            append(" | ")
                            append(viewModel.bloodGroup?.displayName ?: "NA")
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Basic info cannot be changed",
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Age",
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.age?.toString() ?: "",
                        onValueChange = {
                            viewModel.onEvent(
                                EditProfileScreenEvents.OnAgeChange(it)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            EditableChipSection(
                title = "Conditions",
                items = viewModel.conditions,
                input = viewModel.conditionInput,
                onInputChange = {
                    viewModel.onEvent(
                        EditProfileScreenEvents.OnConditionInputChange(it)
                    )
                },
                onAddClick = {
                    viewModel.onEvent(
                        EditProfileScreenEvents.OnAddConditionClick
                    )
                },
                onRemove = {
                    viewModel.onEvent(
                        EditProfileScreenEvents.OnRemoveCondition(it)
                    )
                }
            )

            EditableChipSection(
                title = "Allergies",
                items = viewModel.allergies,
                input = viewModel.allergyInput,
                onInputChange = {
                    viewModel.onEvent(
                        EditProfileScreenEvents.OnAllergyInputChange(it)
                    )
                },
                onAddClick = {
                    viewModel.onEvent(
                        EditProfileScreenEvents.OnAddAllergyClick
                    )
                },
                onRemove = {
                    viewModel.onEvent(
                        EditProfileScreenEvents.OnRemoveAllergy(it)
                    )
                }
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Vitals",
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.bloodPressure,
                        onValueChange = {
                            viewModel.onEvent(
                                EditProfileScreenEvents.OnBloodPressureChange(it)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Blood Pressure")
                        },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.sugar,
                        onValueChange = {
                            viewModel.onEvent(
                                EditProfileScreenEvents.OnSugarChange(it)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Sugar")
                        },
                        singleLine = true
                    )
                }
            }

            ElevatedButton(
                onClick = {
                    viewModel.onEvent(
                        EditProfileScreenEvents.OnSaveChangesClick
                    )
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),

                enabled = viewModel.isBasicInfoValid()
            ) {
                Text("Save Changes")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun EditableChipSection(
    title: String,
    items: List<String>,
    input: String,
    onInputChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onRemove: (String) -> Unit
) {

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = title,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = input,
                    onValueChange = onInputChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.padding(4.dp))

                TextButton(
                    onClick = onAddClick
                ) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items.forEach { item ->

                    AssistChip(
                        onClick = { },

                        label = {
                            Text(item)
                        },

                        trailingIcon = {

                            IconButton(
                                onClick = {
                                    onRemove(item)
                                }
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}