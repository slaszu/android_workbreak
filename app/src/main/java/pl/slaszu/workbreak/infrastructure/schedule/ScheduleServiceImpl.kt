package pl.slaszu.workbreak.infrastructure.schedule

import androidx.activity.ComponentActivity
import pl.slaszu.schedule.Schedule
import pl.slaszu.workbreak.domain.ScheduleService

class ScheduleServiceImpl(
    private val schedule: Schedule
) : ScheduleService {
    override fun hasPermission(): Boolean {
        return schedule.hasPermission()
    }

    override fun prepareRequest(
        activity: ComponentActivity,
        callback: (Boolean) -> Unit
    ) {
        schedule.prepareRequest(
            activity = activity,
            callback = callback
        )
    }

    override fun launchRequest() {
        schedule.launch()
    }
}