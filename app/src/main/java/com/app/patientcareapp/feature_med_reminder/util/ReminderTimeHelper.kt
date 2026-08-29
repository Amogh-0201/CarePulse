package com.app.patientcareapp.feature_med_reminder.util

import java.util.Calendar

object ReminderTimeHelper {

    fun calculateNextTriggerTime(
        hour: Int,
        minute: Int,
        startDateMillis: Long,
        repeatType: String,
        forceNext: Boolean = false
    ): Long {

        val calendar = Calendar.getInstance().apply {
            timeInMillis = startDateMillis
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val currentTime = System.currentTimeMillis()
        
        val daysToAdd = when (repeatType) {
            "DAILY" -> 1
            "EVERY_2_DAYS" -> 2
            "EVERY_3_DAYS" -> 3
            "WEEKLY" -> 7
            else -> 1
        }

        // If forceNext is true, we always move to the next occurrence.
        // This is used when an alarm just fired (even if slightly early) to prevent rescheduling for the same day.
        if (forceNext) {
            calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
        }

        // Ensure the time is strictly in the future (plus a small buffer)
        while (calendar.timeInMillis <= currentTime + 60000) {
            calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
        }

        return calendar.timeInMillis
    }
}