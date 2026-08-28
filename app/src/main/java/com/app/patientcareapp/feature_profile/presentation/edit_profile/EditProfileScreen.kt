package com.app.patientcareapp.feature_profile.presentation.edit_profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.app.patientcareapp.core.presentation.components.AppSnackbarHost

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }

    // Premium Mesh Background
    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
            MaterialTheme.colorScheme.background
        ),
        startY = 0f,
        endY = 1000f
    )

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
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
        snackbarHost = { AppSnackbarHost(hostState = snackBarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // Immutable Info Card (Displays data that cannot be changed)
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = if (isSystemInDarkTheme()) 0.5f else 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Lock, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = viewModel.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${viewModel.gender?.displayName} • ${viewModel.bloodGroup?.displayName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                // Section: Age
                PremiumEditCard(title = "Basic Information", icon = Icons.Rounded.Badge) {
                    OutlinedTextField(
                        value = viewModel.age?.toString() ?: "",
                        onValueChange = { viewModel.onEvent(EditProfileScreenEvents.OnAgeChange(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        label = { Text("Your Age") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = if (isSystemInDarkTheme()) 0.5f else 0.1f)
                        )
                    )
                }

                // Section: Conditions
                EditablePremiumChipSection(
                    title = "Medical Conditions",
                    icon = Icons.Rounded.HealthAndSafety,
                    items = viewModel.conditions,
                    input = viewModel.conditionInput,
                    onInputChange = { viewModel.onEvent(EditProfileScreenEvents.OnConditionInputChange(it)) },
                    onAddClick = { viewModel.onEvent(EditProfileScreenEvents.OnAddConditionClick) },
                    onRemove = { viewModel.onEvent(EditProfileScreenEvents.OnRemoveCondition(it)) },
                    enabled = viewModel.isConditionInputValid()
                )

                // Section: Allergies
                EditablePremiumChipSection(
                    title = "Allergies",
                    icon = Icons.Rounded.WarningAmber,
                    items = viewModel.allergies,
                    input = viewModel.allergyInput,
                    onInputChange = { viewModel.onEvent(EditProfileScreenEvents.OnAllergyInputChange(it)) },
                    onAddClick = { viewModel.onEvent(EditProfileScreenEvents.OnAddAllergyClick) },
                    onRemove = { viewModel.onEvent(EditProfileScreenEvents.OnRemoveAllergy(it)) },
                    enabled = viewModel.isAllergyInputValid(),
                    accentColor = Color(0xFFF59E0B) // Amber for warnings
                )

                // Section: Vitals
                PremiumEditCard(title = "Standard Vitals", icon = Icons.Rounded.MonitorHeart) {
                    OutlinedTextField(
                        value = viewModel.bloodPressure,
                        onValueChange = { viewModel.onEvent(EditProfileScreenEvents.OnBloodPressureChange(it)) },
                        label = { Text("Blood Pressure (mmHg)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        placeholder = { Text("e.g. 120/80") },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = if (isSystemInDarkTheme()) 0.5f else 0.1f)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = viewModel.sugar,
                        onValueChange = { viewModel.onEvent(EditProfileScreenEvents.OnSugarChange(it)) },
                        label = { Text("Sugar Level (mg/dL)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        placeholder = { Text("e.g. 95") },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = if (isSystemInDarkTheme()) 0.5f else 0.1f)
                        )
                    )
                }

                // Save Button
                Button(
                    onClick = { viewModel.onEvent(EditProfileScreenEvents.OnSaveChangesClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(18.dp),
                    enabled = viewModel.isBasicInfoValid(),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun PremiumEditCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.5.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = if (isSystemInDarkTheme()) 0.5f else 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditablePremiumChipSection(
    title: String,
    icon: ImageVector,
    items: List<String>,
    input: String,
    onInputChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onRemove: (String) -> Unit,
    enabled: Boolean,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    PremiumEditCard(title = title, icon = icon) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text("Add new...") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = if (isSystemInDarkTheme()) 0.5f else 0.1f)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onAddClick,
                enabled = enabled,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = accentColor.copy(0.1f),
                    contentColor = accentColor,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(0.05f)
                ),
                modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp))
            ) {
                Icon(Icons.Rounded.Add, "Add")
            }
        }

        if (items.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items.forEach { item ->
                    InputChip(
                        selected = false,
                        onClick = { },
                        label = { Text(item, fontWeight = FontWeight.Medium) },
                        trailingIcon = {
                            Icon(
                                Icons.Rounded.Cancel,
                                "Remove",
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { onRemove(item) }
                            )
                        },
                        shape = CircleShape,
                        // Fix: Explicitly pass enabled and selected states to the border function
                        border = InputChipDefaults.inputChipBorder(
                            selected = false,
                            enabled = true,
                            borderColor = accentColor.copy(alpha = 0.2f),
                            borderWidth = 1.dp
                        ),
                        colors = InputChipDefaults.inputChipColors(
                            labelColor = accentColor,
                            trailingIconColor = accentColor
                        )
                    )
                }
            }
        }
    }
}
