package pl.slaszu.workbreak.domain.schedule

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pl.slaszu.workbreak.domain.model.WorkDay
import pl.slaszu.workbreak.domain.receiver.NotificationDisplayReceiver
import pl.slaszu.workbreak.domain.utils.toEpochMillis

class ScheduleAlarmService @Inject constructor(
    @param:ApplicationContext private val applicationContext: Context,
    private val schedulePermission: SchedulePermissionService
) {

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleBreakAlarm(breakData: BreakScheduleAlarm): LocalDateTime? {
        if (!schedulePermission.hasPermission()) {
            Log.d("myapp", "Schedule permission not granted")
            return null
        }

        val pendingIntent = createPendingIntent(breakData)

        val alarmDateTime = if (breakData.type == BreakScheduleAlarmType.START) {
            breakData.period.start
        } else {
            breakData.period.end
        }

        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmDateTime.toEpochMillis(),
            pendingIntent
        )

        Log.d("myapp", "Schedule alarmDateTime = $alarmDateTime")
        Log.d("myapp", "Schedule alarm SET: $breakData")

        return alarmDateTime
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

    private fun createPendingIntent(item: BreakScheduleAlarm?): PendingIntent {
        return PendingIntent.getBroadcast(
            applicationContext,
            1,
            Intent(applicationContext, NotificationDisplayReceiver::class.java).apply {
                putExtra("BREAK", Json.encodeToString(item))
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

@Serializable
data class BreakScheduleAlarm(
    val period: BreakPeriod,
    val workDay: WorkDay,
    val type: BreakScheduleAlarmType
)

@Serializable
data class BreakPeriod(
    val start: LocalDateTime,
    val end: LocalDateTime
)

enum class BreakScheduleAlarmType {
    START, END
}