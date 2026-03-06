package pl.slaszu.workbreak.domain.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import pl.slaszu.workbreak.MainActivity
import pl.slaszu.workbreak.R
import pl.slaszu.workbreak.application.receiver.MuteForTodayReceiver
import pl.slaszu.workbreak.domain.model.alarm.Alarm
import pl.slaszu.workbreak.domain.presentation.AlarmPresentationFactory

class NotificationService @Inject constructor(
    @param:ApplicationContext private val applicationContext: Context,
    private val notificationPermissionService: NotificationPermissionService,
    private val alarmPresentationFactory: AlarmPresentationFactory
) {

    fun displayNotification(title: String, message: String) {
        if (!notificationPermissionService.hasPermission()) {
            Log.d("myapp", "NotificationService has no permission")
            return
        }

        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(1, getNotification(title, message))
    }

    fun displayNotification(alarm: Alarm) {

        if (!notificationPermissionService.hasPermission()) {
            Log.d("myapp", "NotificationService has no permission")
            return
        }

        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(1, getNotification(alarm))
    }

    private fun getNotification(title: String, message: String): Notification {

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(
            applicationContext,
            notificationPermissionService.channelId()
        )
            .setSmallIcon(R.drawable.cancel_24px)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }

    private fun getNotification(alarm: Alarm): Notification {

        val alarmPresentation = alarmPresentationFactory.create(alarm)

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val muteIntent = Intent(applicationContext, MuteForTodayReceiver::class.java)
        val mutePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            muteIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(
            applicationContext,
            notificationPermissionService.channelId()
        )
            .setSmallIcon(alarmPresentation.icon)
            .setContentTitle(alarmPresentation.header)
            .setContentText(alarmPresentation.description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.cancel_24px, "MUTE FOR TODAY", mutePendingIntent)
            .build()
    }
}
