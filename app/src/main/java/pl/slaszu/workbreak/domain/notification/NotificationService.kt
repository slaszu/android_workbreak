package pl.slaszu.workbreak.domain.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import pl.slaszu.workbreak.domain.model.alarm.Alarm
import pl.slaszu.workbreak.domain.presentation.AlarmPresentationFactory

class NotificationService @Inject constructor(
    @param:ApplicationContext private val applicationContext: Context,
    private val notificationPermissionService: NotificationPermissionService,
    private val alarmPresentationFactory: AlarmPresentationFactory
) {
    fun displayNotification(alarm: Alarm) {

        if (!notificationPermissionService.hasPermission()) {
            Log.d("myapp", "NotificationService has no permission")
            return
        }

        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(1, getNotification(alarm))
    }

    private fun getNotification(alarm: Alarm): Notification {

        val alarmPresentation = alarmPresentationFactory.create(alarm)

        return NotificationCompat.Builder(
            applicationContext,
            notificationPermissionService.channelId()
        )
            .setSmallIcon(alarmPresentation.icon)
            .setContentTitle(alarmPresentation.header)
            .setContentText(alarmPresentation.description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }
}