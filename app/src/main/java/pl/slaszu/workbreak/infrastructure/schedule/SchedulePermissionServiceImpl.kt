package pl.slaszu.workbreak.infrastructure.schedule

import androidx.activity.ComponentActivity
import pl.slaszu.schedule.SchedulePermission
import pl.slaszu.workbreak.domain.schedule.SchedulePermissionService

class SchedulePermissionServiceImpl(
    private val schedulePermission: SchedulePermission
) : SchedulePermissionService {
    override fun hasPermission(): Boolean {
        return schedulePermission.hasPermission()
    }

    override fun prepareRequest(
        activity: ComponentActivity,
        callback: (Boolean) -> Unit
    ) {
        schedulePermission.prepareRequest(
            activity = activity,
            callback = callback
        )
    }

    override fun launchRequest() {
        schedulePermission.launch()
    }
}