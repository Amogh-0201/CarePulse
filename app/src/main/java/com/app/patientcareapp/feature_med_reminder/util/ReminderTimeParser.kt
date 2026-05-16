package com.app.patientcareapp.feature_med_reminder.util

object ReminderTimeParser {

    fun parseTimeToHourMinute(time: String): Pair<Int, Int> {

        val parts = time.split(":")

        val hour = parts[0].toInt()
        val minute = parts[1].toInt()

        return Pair(hour, minute)
    }
}