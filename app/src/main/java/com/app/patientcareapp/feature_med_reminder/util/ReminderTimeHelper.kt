package com.app.patientcareapp.feature_med_reminder.util

import java.util.Calendar

object ReminderTimeHelper {

    fun calculateNextTriggerTime(
        hour: Int,
        minute: Int,
        startDateMillis: Long,
        repeatType: String
    ): Long {

        val calendar = Calendar.getInstance().apply {
            timeInMillis = startDateMillis
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            val daysToAdd = when (repeatType) {
                "DAILY" -> 1
                "EVERY_2_DAYS" -> 2
                "EVERY_3_DAYS" -> 3
                "WEEKLY" -> 7
                else -> 1
            }

            while (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)
            }
        }

        return calendar.timeInMillis
    }
}