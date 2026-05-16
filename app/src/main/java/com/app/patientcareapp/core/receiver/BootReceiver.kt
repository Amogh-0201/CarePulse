package com.app.patientcareapp.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.patientcareapp.feature_med_reminder.data.alarm.MedReminderAlarmManager
import com.app.patientcareapp.feature_med_reminder.data.alarm.MedicineAlarmScheduler
import com.app.patientcareapp.feature_med_reminder.domain.use_case.MedReminderUseCases
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class BootReceiver: BroadcastReceiver() {

    @Inject
    lateinit var useCases: MedReminderUseCases

    override fun onReceive(context: Context, intent: Intent) {

        if(intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {

            // Tell Android to keep this receiver alive while we do background work
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {

                try {

                    val allReminders = useCases.getAllMedRemindersUseCase().firstOrNull()

                    val scheduler = MedicineAlarmScheduler(context)
                    val alarmManager = MedReminderAlarmManager(scheduler)

                    allReminders?.let { medReminders ->
                        medReminders.forEach { reminder ->
                            if(reminder.isActive) {
                                alarmManager.scheduleMedReminder(reminder)
                            }
                        }
                    }

                } catch(e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }

            }

        }
    }
}