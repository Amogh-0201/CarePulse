package com.app.patientcareapp.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.patientcareapp.core.notification.NotificationHelper
import com.app.patientcareapp.feature_med_reminder.data.alarm.MedicineAlarmScheduler
import com.app.patientcareapp.feature_med_reminder.domain.use_case.MedReminderUseCases
import com.app.patientcareapp.feature_med_reminder.util.ReminderTimeHelper
import com.app.patientcareapp.feature_med_reminder.util.ReminderTimeParser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MedicineReminderReceiver: BroadcastReceiver() {

    @Inject
    lateinit var useCases: MedReminderUseCases

    override fun onReceive(context: Context, intent: Intent) {

        val id = intent.getIntExtra("id", -1)
        val index = intent.getIntExtra("index", 0)
        if (id == -1) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reminderId = id / 100
                val reminder = useCases.getMedReminderUseCase(reminderId) ?: return@launch
                val timeString = intent.getStringExtra("time_string") ?: return@launch
                val scheduledAt = intent.getLongExtra("scheduled_at", 0L)

                // An alarm can outlive an edit or a pause. Room is the source of
                // truth, so stale alarms must not notify or create a new alarm.
                if (!reminder.isActive ||
                    index !in reminder.times.indices ||
                    reminder.times[index] != timeString ||
                    (reminder.endDate != null &&
                        (System.currentTimeMillis() > reminder.endDate ||
                            (scheduledAt > 0L && scheduledAt > reminder.endDate)))
                ) return@launch

                val (hour, minute) = ReminderTimeParser.parseTimeToHourMinute(timeString)
                NotificationHelper.showNotification(
                    context,
                    reminder.medicineName,
                    "Take your ${reminder.medicineName} (${reminder.dosage})"
                )

                val nextTriggerTime = ReminderTimeHelper.calculateNextTriggerTime(
                    hour = hour,
                    minute = minute,
                    startDateMillis = if (scheduledAt > 0L) {
                        scheduledAt
                    } else {
                        System.currentTimeMillis()
                    },
                    repeatType = reminder.repeatType,
                    forceNext = true
                )

                if (reminder.endDate == null || nextTriggerTime <= reminder.endDate) {
                    MedicineAlarmScheduler(context).scheduleReminder(
                        id = id,
                        triggerTimeMillis = nextTriggerTime,
                        title = reminder.medicineName,
                        message = "Take your ${reminder.medicineName}",
                        dosage = reminder.dosage,
                        repeatType = reminder.repeatType,
                        endDate = reminder.endDate,
                        timeString = timeString,
                        index = index
                    )
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

}