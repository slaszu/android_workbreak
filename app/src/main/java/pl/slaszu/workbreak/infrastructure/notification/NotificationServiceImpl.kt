package pl.slaszu.workbreak.infrastructure.notification

import androidx.activity.ComponentActivity
import pl.slaszu.notification.Notification
import pl.slaszu.workbreak.domain.NotificationService

class NotificationServiceImpl(
    private val notification: Notification
) : NotificationService {
    override fun registerChannel() {
        notification.registerChannel()
    }

    override fun hasPermission(): Boolean {
        return notification.hasPermission()
    }

    override fun shouldShowRationale(activity: ComponentActivity): Boolean {
        return notification.shouldShowRationale(activity)
    }

    override fun prepareRequest(
        activity: ComponentActivity,
        callback: (Boolean) -> Unit
    ) {
        notification.prepareRequest(activity, callback)
    }

    override fun launchRequest() {
        notification.launchRequest()
    }
}