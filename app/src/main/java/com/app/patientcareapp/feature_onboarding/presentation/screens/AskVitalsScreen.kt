package com.app.patientcareapp.feature_onboarding.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bloodtype
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.patientcareapp.core.presentation.components.OnboardingCard
import com.app.patientcareapp.core.presentation.components.OnboardingProgress
import com.app.patientcareapp.feature_onboarding.presentation.events.OnBoardingScreenEvents
import com.app.patientcareapp.feature_onboarding.presentation.viewmodels.OnBoardingViewModel
import com.app.patientcareapp.ui.theme.PrimaryBlue
import com.app.patientcareapp.ui.theme.SecondaryTeal

@Composable
fun AskVitalsScreen(
    navController: NavController
) {
    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry("onboarding")
    }
    val viewModel: OnBoardingViewModel = hiltViewModel(parentEntry)
    val focusManager = LocalFocusManager.current

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
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
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
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Step Indicator (Step 3 of 4)
                OnboardingProgress(currentStep = 3, totalSteps = 4)

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Medical Vitals",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Enter your most recent readings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                OnboardingCard(title = "Health Readings", icon = Icons.Rounded.MonitorHeart) {
                    OutlinedTextField(
                        value = viewModel.bloodPressure,
                        onValueChange = {
                            viewModel.onEvent(OnBoardingScreenEvents.OnBloodPressureChange(it))
                        },
                        label = { Text("Blood Pressure (mmHg)") },
                        placeholder = { Text("e.g. 120/80") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = {
                            Icon(Icons.Rounded.Bloodtype, null, tint = MaterialTheme.colorScheme.primary)
                        },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.sugar,
                        onValueChange = {
                            viewModel.onEvent(OnBoardingScreenEvents.OnSugarChange(it))
                        },
                        label = { Text("Sugar Level (mg/dL)") },
                        placeholder = { Text("e.g. 95") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = {
                            Icon(Icons.Rounded.Opacity, null, tint = MaterialTheme.colorScheme.primary)
                        },
                        singleLine = true
                    )
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
                        onClick = { navController.navigate("ask_allergies_screen") },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = if (viewModel.isVitalsEmpty()) "Skip" else "Continue",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}