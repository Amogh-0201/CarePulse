package com.app.patientcareapp.feature_onboarding.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Person
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.patientcareapp.core.presentation.components.DatePickerField
import com.app.patientcareapp.core.presentation.components.OnboardingCard
import com.app.patientcareapp.core.presentation.components.OnboardingProgress
import com.app.patientcareapp.core.presentation.components.SelectionDropdown
import com.app.patientcareapp.core.util.DateUtils
import com.app.patientcareapp.feature_onboarding.presentation.events.OnBoardingScreenEvents
import com.app.patientcareapp.feature_onboarding.presentation.viewmodels.OnBoardingViewModel
import com.app.patientcareapp.feature_profile.domain.model.BloodGroup
import com.app.patientcareapp.feature_profile.domain.model.Gender
import com.app.patientcareapp.ui.theme.PrimaryBlue
import com.app.patientcareapp.ui.theme.SecondaryTeal

@Composable
fun BasicInfoScreen(
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
        Box(modifier = Modifier.fillMaxSize().background(bgGradient).padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Step Indicator
                OnboardingProgress(currentStep = 1, totalSteps = 4)

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Personal Details",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Let's start with some basic information about you.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                OnboardingCard(title = "Profile Info", icon = Icons.Rounded.Person) {
                    OutlinedTextField(
                        value = viewModel.name,
                        onValueChange = { viewModel.onEvent(OnBoardingScreenEvents.OnNameChange(it)) },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SelectionDropdown<Gender>(
                            modifier = Modifier.weight(1f),
                            label = "Gender",
                            options = Gender.entries,
                            selectedOption = viewModel.gender,
                            optionLabel = { it.displayName },
                            onOptionSelected = {
                                viewModel.onEvent(OnBoardingScreenEvents.OnGenderChange(it))
                            }
                        )
                        SelectionDropdown<BloodGroup>(
                            modifier = Modifier.weight(1f),
                            label = "Blood Group",
                            options = BloodGroup.entries,
                            selectedOption = viewModel.bloodGroup,
                            optionLabel = { it.displayName },
                            onOptionSelected = {
                                viewModel.onEvent(OnBoardingScreenEvents.OnBloodGroupChange(it))
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    DatePickerField(
                        label = "Date of Birth",
                        selectedDate = viewModel.dateOfBirth,
                        onDateSelected = {
                            viewModel.onEvent(OnBoardingScreenEvents.OnDateOfBirthChange(it))
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Back", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { navController.navigate("ask_conditions_screen") },
                        enabled = viewModel.isBasicInfoValid(),
                        modifier = Modifier.weight(1.5f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text("Continue", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
