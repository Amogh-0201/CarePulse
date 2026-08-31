package com.app.patientcareapp.feature_med_reminder.data.alarm

import com.app.patientcareapp.feature_med_reminder.domain.model.MedReminder
import com.app.patientcareapp.feature_med_reminder.util.ReminderTimeHelper
import com.app.patientcareapp.feature_med_reminder.util.ReminderTimeParser

class MedReminderAlarmManager(
    private val scheduler: MedicineAlarmScheduler
) {

    fun scheduleMedReminder(reminder: MedReminder) {
        if (!reminder.isActive || reminder.id == null) return

        reminder.times.forEachIndexed { index, time ->
            val (hour, minute) = ReminderTimeParser.parseTimeToHourMinute(time)

            val triggerTime = ReminderTimeHelper.calculateNextTriggerTime(
                hour,
                minute,
                reminder.startDate,
                reminder.repeatType)

            // Skip scheduling if the calculated trigger time is already past the end date
            if (reminder.endDate != null && triggerTime > reminder.endDate) {
                return@forEachIndexed
            }

            scheduler.scheduleReminder(
                id = reminder.id * 100 + index,
                triggerTimeMillis = triggerTime,
                title = reminder.medicineName,
                message = "Take your ${reminder.medicineName}",
                dosage = reminder.dosage,
                repeatType = reminder.repeatType,
                endDate = reminder.endDate,
                timeString = time,
                index = index
            )
        }
    }

    fun cancelMedReminder(reminder: MedReminder) {
        if (reminder.id == null) return
        reminder.times.forEachIndexed { index, _ ->
            scheduler.cancelReminder(reminder.id * 100 + index)
        }
    }

    fun hasExactAlarmPermission(): Boolean {
        return scheduler.hasExactAlarmPermission()
    }

    fun openExactAlarmSettings() {
        scheduler.openExactAlarmSettings()
    }
}
