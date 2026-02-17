package pl.slaszu.workbreak.domain.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import pl.slaszu.workbreak.R
import pl.slaszu.workbreak.domain.schedule.BreakScheduleAlarm
import pl.slaszu.workbreak.domain.schedule.BreakScheduleAlarmType

class NotificationService @Inject constructor(
    @param:ApplicationContext private val applicationContext: Context,
    private val notificationPermissionService: NotificationPermissionService
) {
    fun displayNotification(breakScheduleAlarm: BreakScheduleAlarm) {

        if (!notificationPermissionService.hasPermission()) {
            Log.d("myapp", "NotificationService has no permission")
            return
        }

        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(1, getNotification(breakScheduleAlarm))
    }

    private fun getNotification(breakScheduleAlarm: BreakScheduleAlarm): Notification {

        var icon = R.drawable.baseline_timer_24
        if (breakScheduleAlarm.type == BreakScheduleAlarmType.END) {
            icon = R.drawable.baseline_timer_off_24
        }

        var textTitle = "Break start"
        if (breakScheduleAlarm.type == BreakScheduleAlarmType.END) {
            textTitle = "Break end"
        }

        var textContent = "6th break at thursday"


        return NotificationCompat.Builder(
            applicationContext,
            notificationPermissionService.channelId()
        )
            .setSmallIcon(icon)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }
}