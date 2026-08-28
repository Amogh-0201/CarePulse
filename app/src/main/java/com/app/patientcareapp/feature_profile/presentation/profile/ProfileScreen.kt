package com.app.patientcareapp.feature_profile.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.app.patientcareapp.ui.theme.PrimaryBlue
import com.app.patientcareapp.ui.theme.SecondaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavController
) {
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is ProfileViewModel.UiEvent.NavigateToEditProfileScreen -> {
                    navController.navigate("edit_profile")
                }
            }
        }
    }

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
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.onEvent(ProfileScreenEvents.OnEditProfileClick) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                icon = { Icon(Icons.Rounded.Edit, "Edit") },
                text = { Text("Edit Profile", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .padding(paddingValues)) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    ProfileHeader(
                        name = viewModel.name,
                        age = viewModel.age,
                        gender = viewModel.gender?.displayName,
                        bloodGroup = viewModel.bloodGroup?.displayName
                    )
                }

                item {
                    PremiumProfileCard(title = "Health Vitals", icon = Icons.Rounded.MonitorHeart) {
                        VitalDisplayItem(
                            label = "Blood Pressure",
                            value = viewModel.bloodPressure,
                            unit = "mmHg",
                            icon = Icons.Rounded.Bloodtype,
                            iconColor = Color(0xFFEF4444)
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        VitalDisplayItem(
                            label = "Sugar Level",
                            value = viewModel.sugar,
                            unit = "mg/dL",
                            icon = Icons.Rounded.Opacity,
                            iconColor = Color(0xFF3B82F6)
                        )
                    }
                }

                item {
                    PremiumProfileCard(title = "Medical Conditions", icon = Icons.Rounded.HealthAndSafety) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (viewModel.conditions.isEmpty() || viewModel.conditions.first() == "NA") {
                                Text("No conditions reported", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            } else {
                                viewModel.conditions.forEach { condition -> ProfileChip(text = condition) }
                            }
                        }
                    }
                }

                item {
                    PremiumProfileCard(title = "Allergies", icon = Icons.Rounded.WarningAmber) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (viewModel.allergies.isEmpty() || viewModel.allergies.first() == "NA") {
                                Text("No allergies reported", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            } else {
                                viewModel.allergies.forEach { allergy -> ProfileChip(text = allergy, color = Color(0xFFF59E0B)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, age: Int?, gender: String?, bloodGroup: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Person, null, modifier = Modifier.size(60.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(16.dp))
        Text(name.ifBlank { "User" }, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoTag(text = age?.let { "$it Years" } ?: "Age NA")
            InfoTag(text = gender ?: "Gender NA")
            InfoTag(text = bloodGroup ?: "BG NA", color = Color(0xFFEF4444))
        }
    }
}

@Composable
fun PremiumProfileCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.5.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun VitalDisplayItem(label: String, value: String, unit: String, icon: ImageVector, iconColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(iconColor.copy(0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = iconColor)
            }
            Spacer(Modifier.width(12.dp))
            Text(label, fontWeight = FontWeight.Medium)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(if(value.isBlank() || value == "NA") "--" else value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (value.isNotBlank() && value != "NA") Text(unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun ProfileChip(text: String, color: Color = MaterialTheme.colorScheme.primary) {
    Surface(shape = CircleShape, color = color.copy(0.05f), border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(0.1f))) {
        Text(text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelLarge, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun InfoTag(text: String, color: Color = MaterialTheme.colorScheme.primary) {
    Surface(color = color.copy(0.1f), shape = RoundedCornerShape(8.dp)) {
        Text(text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.Bold)
    }
}