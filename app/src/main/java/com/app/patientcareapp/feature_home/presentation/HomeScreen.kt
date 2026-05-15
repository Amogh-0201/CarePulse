package com.app.patientcareapp.feature_home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Greeting Section
            item {
                Column {
                    Text(
                        text = "Welcome Back 👋",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = viewModel.userName.ifBlank { "User" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Health Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "Health Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            Column {
                                Text("Blood Group")
                                Text(
                                    text = viewModel.bloodGroup,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Column {
                                Text("Records")
                                Text(
                                    text = viewModel.totalHealthRecords.toString(),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Upcoming Medicine Card
            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "Upcoming Medicine",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val upcomingMedicine =
                            viewModel.upcomingMedicine

                        if (upcomingMedicine != null) {

                            Text(
                                text = upcomingMedicine
                                    .medReminder
                                    .medicineName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Time: ${
                                    upcomingMedicine.upcomingTime
                                }"
                            )

                            Text(
                                text = "Dosage: ${
                                    upcomingMedicine
                                        .medReminder
                                        .dosage
                                }"
                            )

                        } else {

                            Text(
                                text = "No upcoming medicines"
                            )
                        }
                    }
                }
            }

            // Today's Medicines
            item {

                Text(
                    text = "Today's Medicines",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (viewModel.todayMedicines.isEmpty()) {

                item {

                    Text(
                        text = "No medicines for today"
                    )
                }

            } else {

                items(viewModel.todayMedicines.size) { index ->

                    val medicine =
                        viewModel.todayMedicines[index]

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = medicine.medicineName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Dosage: ${medicine.dosage}"
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Times"
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            medicine.times.forEach { time ->

                                Text(text = time)
                            }
                        }
                    }
                }
            }

            // Bottom Space
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}