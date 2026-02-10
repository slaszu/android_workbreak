package pl.slaszu.workbreak.domain.schedule

import androidx.activity.ComponentActivity

interface SchedulePermissionService {
    fun hasPermission(): Boolean
    fun prepareRequest(activity: ComponentActivity, callback: (Boolean) -> Unit)
    fun launchRequest()
}
