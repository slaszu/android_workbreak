package pl.slaszu.workbreak.domain

import androidx.activity.ComponentActivity

interface NotificationService {
    fun registerChannel()
    fun hasPermission(): Boolean
    fun shouldShowRationale(activity: ComponentActivity): Boolean
    fun prepareRequest(activity: ComponentActivity, callback: (Boolean) -> Unit)
    fun launchRequest()
}