package com.app.patientcareapp.feature_home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.patientcareapp.core.data.preferences.PreferenceManager
import com.app.patientcareapp.feature_health_records.domain.use_case.HealthRecordUseCases
import com.app.patientcareapp.feature_med_reminder.domain.model.MedReminder
import com.app.patientcareapp.feature_med_reminder.domain.use_case.MedReminderUseCases
import com.app.patientcareapp.feature_profile.domain.use_case.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val medReminderUseCases: MedReminderUseCases,
    private val healthRecordUseCases: HealthRecordUseCases,
    private val preferenceManager: PreferenceManager
): ViewModel() {

    var userName by mutableStateOf("")
        private set

    var bloodGroup by mutableStateOf("")
        private set

    var todayMedicines by mutableStateOf<List<MedReminder>>(emptyList())
        private set

    var upcomingMedicine by mutableStateOf<UpcomingMedicine?>(null)
        private set

    var totalHealthRecords by mutableIntStateOf(0)
        private set

    val isBatteryWarningDismissed = preferenceManager.isBatteryWarningDismissed
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun dismissBatteryWarning() {
        viewModelScope.launch {
            preferenceManager.setBatteryWarningDismissed(true)
        }
    }

    init {
        loadProfile()
        loadMedicines()
        loadHealthRecords()
    }


    private fun loadProfile() {
        viewModelScope.launch {
            profileUseCases.getProfileUseCase().collectLatest { profile ->
                profile?.let {
                    userName = it.name
                    bloodGroup = it.bloodGroup.displayName
                }
            }
        }
    }

    private fun loadMedicines() {
        viewModelScope.launch {
            val ticker = flow {
                while (true) {
                    emit(Unit)
                    delay(30_000) // Refresh every 30 seconds to catch time-based updates
                }
            }

            combine(
                medReminderUseCases.getAllMedRemindersUseCase(),
                ticker
            ) { medReminders, _ ->
                medReminders
            }.collectLatest { medReminders ->
                todayMedicines = medReminders.filter {
                    isReminderForToday(it)
                }
                upcomingMedicine = getUpcomingMedicine(todayMedicines)
            }
        }
    }

    private fun loadHealthRecords() {
        viewModelScope.launch {
            healthRecordUseCases.getAllHealthRecordsUseCase().collectLatest { healthRecords ->
                totalHealthRecords = healthRecords.size
            }
        }
    }

    //helper model
    data class UpcomingMedicine(
        val medReminder: MedReminder,
        val upcomingTime: String
    )


    //helper functions
    private fun millisToCalender(millis: Long): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = millis
        }
    }

    private fun getDaysDifference(
        startMillis: Long,
        currentMillis: Long
    ): Long {
        val diff = currentMillis - startMillis

        return TimeUnit.MILLISECONDS.toDays(diff)
    }

    private fun isReminderForToday(reminder: MedReminder): Boolean {

        if(!reminder.isActive) return false

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val startDate = millisToCalender(reminder.startDate).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val endDate = reminder.endDate?.let {
            millisToCalender(it).apply {
                set(Calendar.HOUR_OF_DAY, 23) // End of the day
                set(Calendar.MINUTE, 59)
            }
        }

        // before start date
        if (today.before(startDate)) {
            return false
        }

        // after end date
        if (endDate != null && today.after(endDate)) {
            return false
        }

        val daysDifference = getDaysDifference(
            startDate.timeInMillis,
            today.timeInMillis
        )

        return when (reminder.repeatType) {

            "DAILY" -> true

            "EVERY_2_DAYS" -> {
                daysDifference % 2 == 0L
            }

            "EVERY_3_DAYS" -> {
                daysDifference % 3 == 0L
            }

            "WEEKLY" -> {
                today.get(Calendar.DAY_OF_WEEK) ==
                        startDate.get(Calendar.DAY_OF_WEEK)
            }

            else -> false
        }
    }

    private fun getUpcomingMedicine(
        reminders: List<MedReminder>
    ): UpcomingMedicine? {

        val currentCalendar = Calendar.getInstance()

        var nearestMedicine: UpcomingMedicine? = null

        var nearestTimeInMillis: Long? = null

        reminders.forEach { reminder ->
            reminder.times.forEach { timeString ->
                try {
                    val splitTime = timeString.split(":")

                    val hour = splitTime[0].toInt()

                    val minute = splitTime[1].toInt()

                    val reminderCalendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    val reminderTimeMillis =
                        reminderCalendar.timeInMillis

                    if (reminderTimeMillis >
                        currentCalendar.timeInMillis
                    ) {
                        if (
                            nearestTimeInMillis == null ||
                            reminderTimeMillis < nearestTimeInMillis!!
                        ) {
                            nearestTimeInMillis =
                                reminderTimeMillis
                            nearestMedicine = UpcomingMedicine(
                                medReminder = reminder,
                                upcomingTime = timeString
                            )
                        }
                    }
                } catch( _: Exception) {}
            }
        }
        return nearestMedicine
    }

}