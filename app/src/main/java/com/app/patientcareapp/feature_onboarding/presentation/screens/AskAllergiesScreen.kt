package com.app.patientcareapp.feature_onboarding.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.patientcareapp.core.util.Screen
import com.app.patientcareapp.feature_onboarding.presentation.events.OnBoardingScreenEvents
import com.app.patientcareapp.feature_onboarding.presentation.viewmodels.OnBoardingViewModel

@Composable
fun AskAllergiesScreen(
    navController: NavController
) {

    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry("onboarding")
    }
    val viewModel: OnBoardingViewModel = hiltViewModel(parentEntry)

    val snackBarHostState = remember{ SnackbarHostState() }

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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {

                Text(
                    text = "Do you have any allergies ?",
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedTextField(
                        value = viewModel.allergyInput,
                        onValueChange = { viewModel.onEvent(OnBoardingScreenEvents.OnAllergyInputChange(it)) },
                        label = { Text("Add Allergies") },
                        placeholder = { Text("Enter allergy you have") },
                        modifier = Modifier.weight(2f),
                        supportingText = { Text("Optional") }
                    )

                    Button(
                        onClick = {
                            viewModel.onEvent(OnBoardingScreenEvents.OnAddAllergy)
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        enabled = viewModel.isAllergyInputValid()
                    ) {
                        Text("Add")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(viewModel.allergies) {allergy ->

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(allergy)

                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove Allergy",
                                    modifier = Modifier.clickable {
                                        viewModel.onEvent(
                                            OnBoardingScreenEvents.OnRemoveAllergy(allergy)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = {navController.popBackStack()},
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }
                Button(
                    onClick = {
                        viewModel.onEvent(OnBoardingScreenEvents.OnFinishButtonClick)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Finish")
                }
            }
        }
    }
}