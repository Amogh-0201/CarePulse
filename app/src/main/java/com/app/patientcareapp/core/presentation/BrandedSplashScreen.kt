package com.app.patientcareapp.core.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.patientcareapp.R
import com.app.patientcareapp.ui.theme.PrimaryBlue
import com.app.patientcareapp.ui.theme.SecondaryTeal
import kotlinx.coroutines.delay

@Composable
fun BrandedSplashScreen(
    onAnimationFinished: () -> Unit
) {
    var startLogoAnimation by remember { mutableStateOf(false) }
    var startNameAnimation by remember { mutableStateOf(false) }
    var startTaglineAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300) // Initial silence
        startLogoAnimation = true
        delay(600) // Reveal Name
        startNameAnimation = true
        delay(700) // Reveal Tagline
        startTaglineAnimation = true
        delay(1800) // Stay for a moment
        onAnimationFinished()
    }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 1f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 1f),
            MaterialTheme.colorScheme.background
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // 1. The Branded App Logo
            AnimatedVisibility(
                visible = startLogoAnimation,
                enter = fadeIn(tween(1000)) + scaleIn(tween(1000), initialScale = 0.8f)
            ) {
                // Using your ic_launcher_foreground vector
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "CarePulse Logo",
                    modifier = Modifier.size(140.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. The App Name: CarePulse
            AnimatedVisibility(
                visible = startNameAnimation,
                enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { it / 2 }
            ) {
                Text(
                    text = "CarePulse",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. The Tagline
            AnimatedVisibility(
                visible = startTaglineAnimation,
                enter = fadeIn(tween(1000)) + slideInVertically(tween(1000)) { it / 3 }
            ) {
                Text(
                    text = "Your Health, Our Responsibility",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}