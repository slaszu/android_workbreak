package pl.slaszu.workbreak.infrastructure.notification

import androidx.activity.ComponentActivity
import pl.slaszu.notification.NotificationPermission
import pl.slaszu.workbreak.domain.notification.NotificationPermissionService

class NotificationPermissionServiceImpl(
    private val notificationPermission: NotificationPermission
) : NotificationPermissionService {
    override fun registerChannel() {
        notificationPermission.registerChannel()
    }

    override fun hasPermission(): Boolean {
        return notificationPermission.hasPermission()
    }

    override fun shouldShowRationale(activity: ComponentActivity): Boolean {
        return notificationPermission.shouldShowRationale(activity)
    }

    override fun prepareRequest(
        activity: ComponentActivity,
        callback: (Boolean) -> Unit
    ) {
        notificationPermission.prepareRequest(activity, callback)
    }

    override fun launchRequest() {
        notificationPermission.launchRequest()
    }
}