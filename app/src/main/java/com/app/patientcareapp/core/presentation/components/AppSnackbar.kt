package com.app.patientcareapp.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AppSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    val actionLabel = snackbarData.visuals.actionLabel

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val containerColor = if (isDark) {
        Color(0xFF28364A)
    } else {
        Color(0xFFFFFFFF)
    }

    val contentColor = if (isDark) {
        Color(0xFFF5F7FB)
    } else {
        Color(0xFF172033)
    }

    // General-purpose icon colors
    val iconTint = if (isDark) {
        Color(0xFF6EA8FF)
    } else {
        Color(0xFF2563EB)
    }

    val iconBackground = if (isDark) {
        Color(0xFF30486A)
    } else {
        Color(0xFFEAF1FF)
    }

    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 8.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // General-purpose notification/info icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = iconBackground,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Snackbar message
            Text(
                text = snackbarData.visuals.message,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )

            // Action button
            if (actionLabel != null) {
                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = {
                        snackbarData.performAction()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 14.dp,
                        vertical = 8.dp
                    )
                ) {
                    Text(
                        text = actionLabel.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}