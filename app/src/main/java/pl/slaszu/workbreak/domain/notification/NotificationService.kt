package pl.slaszu.workbreak.domain.notification

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import pl.slaszu.workbreak.domain.schedule.BreakScheduleAlarm

class NotificationService @Inject constructor(
    @param:ApplicationContext private val applicationContext: Context,
    private val notificationPermissionService: NotificationPermissionService
) {
    fun displayNotification(breakScheduleAlarm: BreakScheduleAlarm) {
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//        notificationManager.notify(
//            1,
//            NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle(textTitle)
//                .setContentText(textContent)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .
//        )
    }
}