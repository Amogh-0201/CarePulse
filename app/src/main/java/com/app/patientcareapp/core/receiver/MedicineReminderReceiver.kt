package com.app.patientcareapp.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.patientcareapp.core.notification.NotificationHelper
import com.app.patientcareapp.feature_med_reminder.data.alarm.MedicineAlarmScheduler
import com.app.patientcareapp.feature_med_reminder.util.ReminderTimeHelper
import com.app.patientcareapp.feature_med_reminder.util.ReminderTimeParser

class MedicineReminderReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getIntExtra("id", -1)
        val title = intent.getStringExtra("title") ?: "Medicine Time"
        val message = intent.getStringExtra("message") ?: "Take your medicine"

        // Extracting your MedReminder field variables
        val dosage = intent.getStringExtra("dosage") ?: ""
        val repeatType = intent.getStringExtra("repeat_type") ?: "DAILY"
        val endDate = intent.getLongExtra("end_date", -1L)
        val timeString = intent.getStringExtra("time_string") ?: ""
        val index = intent.getIntExtra("index", 0)

        // 1. Immediately pop up the notification to the user
        NotificationHelper.showNotification(context, title, "$message ($dosage)")
        if (timeString.isNotBlank() && id != -1) {
            val (hour, minute) = ReminderTimeParser.parseTimeToHourMinute(timeString)

            // Base it on the current time (since the past occurrence just finished)
            val nextTriggerTime = ReminderTimeHelper.calculateNextTriggerTime(
                hour = hour,
                minute = minute,
                startDateMillis = System.currentTimeMillis(),
                repeatType = repeatType
            )

            // 3. THE BOUNDARY CHECK: Only re-schedule if the next run is before or on the expiration date
            if (endDate == -1L || nextTriggerTime <= endDate) {
                val scheduler = MedicineAlarmScheduler(context)
                scheduler.scheduleReminder(
                    id = id,
                    triggerTimeMillis = nextTriggerTime,
                    title = title,
                    message = message,
                    dosage = dosage,
                    repeatType = repeatType,
                    endDate = if (endDate == -1L) null else endDate,
                    timeString = timeString,
                    index = index
                )
            } else {
                // If the next trigger crosses the endDate, we don't call scheduleReminder.
                // The alarm chain naturally terminates here.
            }
        }
    }
}