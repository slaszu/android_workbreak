package pl.slaszu.workbreak.domain.notification

import androidx.activity.ComponentActivity

interface NotificationPermissionService {
    fun registerChannel()
    fun hasPermission(): Boolean
    fun shouldShowRationale(activity: ComponentActivity): Boolean
    fun prepareRequest(activity: ComponentActivity, callback: (Boolean) -> Unit)
    fun launchRequest()
}