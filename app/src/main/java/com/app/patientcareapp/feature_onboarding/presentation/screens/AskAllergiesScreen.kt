package com.app.patientcareapp.feature_onboarding.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.WarningAmber
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.patientcareapp.core.util.Screen
import com.app.patientcareapp.feature_onboarding.presentation.events.OnBoardingScreenEvents
import com.app.patientcareapp.feature_onboarding.presentation.viewmodels.OnBoardingViewModel
import com.app.patientcareapp.ui.theme.PrimaryBlue
import com.app.patientcareapp.ui.theme.SecondaryTeal

@Composable
fun AskAllergiesScreen(
    navController: NavController
) {
    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry("onboarding")
    }
    val viewModel: OnBoardingViewModel = hiltViewModel(parentEntry)
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { uiEvent ->
            when(uiEvent) {
                is OnBoardingViewModel.UiEvent.NavigateToMainApp -> {
                    navController.navigate(Screen.Home.route) {
                        popUpTo("onboarding") {
                            inclusive = true
                        }
                    }
                }
                is OnBoardingViewModel.UiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(message = uiEvent.message)
                }
            }
        }
    }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            PrimaryBlue.copy(alpha = 0.08f),
            SecondaryTeal.copy(alpha = 0.04f),
            MaterialTheme.colorScheme.background
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Step Indicator (Final Step 4 of 4)
                OnboardingProgress(currentStep = 4, totalSteps = 4)

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Medical Allergies",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Finally, do you have any drug or food allergies we should be aware of?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Using WarningAmber for Allergies
                OnboardingCard(title = "Add Allergy", icon = Icons.Rounded.WarningAmber) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = viewModel.allergyInput,
                            onValueChange = {
                                viewModel.onEvent(OnBoardingScreenEvents.OnAllergyInputChange(it))
                            },
                            placeholder = { Text("Enter allergy") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        IconButton(
                            onClick = { viewModel.onEvent(OnBoardingScreenEvents.OnAddAllergy) },
                            enabled = viewModel.isAllergyInputValid(),
                            modifier = Modifier
                                .size(52.dp)
                                .background(
                                    color = if (viewModel.isAllergyInputValid())
                                        Color(0xFFF59E0B).copy(alpha = 0.1f)
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Add",
                                tint = if (viewModel.isAllergyInputValid())
                                    Color(0xFFF59E0B)
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }
                    }

                    if (viewModel.allergies.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            viewModel.allergies.forEach { allergy ->
                                AllergyChip(
                                    text = allergy,
                                    onRemove = {
                                        viewModel.onEvent(OnBoardingScreenEvents.OnRemoveAllergy(allergy))
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Back", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { viewModel.onEvent(OnBoardingScreenEvents.OnFinishButtonClick) },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "Complete",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AllergyChip(
    text: String,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF59E0B).copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = Color(0xFFF59E0B).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFD97706)
            )
            Icon(
                imageVector = Icons.Rounded.Cancel,
                contentDescription = "Remove",
                tint = Color(0xFFF59E0B).copy(alpha = 0.5f),
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onRemove() }
            )
        }
    }
}