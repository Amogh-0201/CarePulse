package com.app.patientcareapp.feature_onboarding.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.patientcareapp.feature_onboarding.presentation.events.OnBoardingScreenEvents
import com.app.patientcareapp.feature_onboarding.presentation.viewmodels.OnBoardingViewModel

@Composable
fun AskVitalsScreen(
    navController: NavController
) {

    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry("onboarding")
    }
    val viewModel: OnBoardingViewModel = hiltViewModel(parentEntry)

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

             Column(
                 modifier = Modifier.weight(1f)
             ) {

                 Text(
                     text = "Enter your Blood Pressure(BP) and Sugar",
                     fontSize = 20.sp
                 )

                 Spacer(modifier = Modifier.height(16.dp))

                 OutlinedTextField(
                     value = viewModel.bloodPressure,
                     onValueChange = { viewModel.onEvent(OnBoardingScreenEvents.OnBloodPressureChange(it)) },
                     label = { Text("Blood Pressure(mmHg)") },
                     placeholder = { Text("Enter your last known blood pressure") },
                     modifier = Modifier.fillMaxWidth(),
                     supportingText = { Text("Optional") }
                 )

                 Spacer(modifier = Modifier.height(10.dp))

                 OutlinedTextField(
                     value = viewModel.sugar,
                     onValueChange = { viewModel.onEvent(OnBoardingScreenEvents.OnSugarChange(it)) },
                     label = { Text("Sugar(mg/dL)") },
                     placeholder = { Text("Enter your last known Sugar") },
                     modifier = Modifier.fillMaxWidth(),
                     supportingText = { Text("Optional") }
                 )

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
                    onClick = {navController.navigate("ask_allergies_screen")},
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        if(viewModel.isVitalsEmpty()) "Skip" else "Next"
                    )
                }
            }
        }
    }
}