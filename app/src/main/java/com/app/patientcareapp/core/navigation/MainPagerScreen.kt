package com.app.patientcareapp.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.app.patientcareapp.feature_health_records.presentation.health_records.HealthRecordsScreen
import com.app.patientcareapp.feature_home.presentation.HomeScreen
import com.app.patientcareapp.feature_med_reminder.presentation.show_med_reminders.MedReminderScreen
import kotlinx.coroutines.launch

@Composable
fun MainPagerScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    pagerState: PagerState,
    onMedicineClick: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 2 // Keep all 3 screens in memory for smooth swiping
    ) { page ->
        when (page) {
            0 -> HealthRecordsScreen(navController = navController)
            1 -> HomeScreen(
                navController = navController,
                paddingValues = paddingValues,
                onMedicineClick = onMedicineClick,
                onRecordsCountClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                }
            )
            2 -> MedReminderScreen(navController = navController)
        }
    }
}
