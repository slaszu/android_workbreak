package pl.slaszu.workbreak.domain.schedule

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.serialization.json.Json
import pl.slaszu.workbreak.domain.model.alarm.Alarm
import pl.slaszu.workbreak.domain.receiver.NotificationDisplayReceiver
import pl.slaszu.workbreak.domain.utils.toEpochMillis

class ScheduleAlarmService @Inject constructor(
    @param:ApplicationContext private val applicationContext: Context,
    private val schedulePermission: SchedulePermissionService
) {

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleBreakAlarm(alarm: Alarm) {
        if (!schedulePermission.hasPermission()) {
            Log.d("myapp", "Schedule permission not granted")
            return
        }

        val pendingIntent = createPendingIntent(alarm)

        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarm.alarmDateTime.toEpochMillis(),
            pendingIntent
        )

        Log.d("myapp", "Schedule alarm SET: $alarm")
    }

    fun cancelAllAlarms() {
        if (!schedulePermission.hasPermission()) {
            Log.d("myapp", "Schedule permission not granted")
            return
        }

        val pendingIntent = createPendingIntent(null)

        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)

        Log.d("myapp", "Schedule all alarms CANCELLED")
    }

    private fun createPendingIntent(item: Alarm?): PendingIntent {
        return PendingIntent.getBroadcast(
            applicationContext,
            1,
            Intent(applicationContext, NotificationDisplayReceiver::class.java).apply {
                putExtra("ALARM", Json.encodeToString(item))
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}