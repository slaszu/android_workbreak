package pl.slaszu.workbreak.domain.presentation

import pl.slaszu.workbreak.domain.Clock
import pl.slaszu.workbreak.domain.model.setting.Setting
import pl.slaszu.workbreak.domain.notification.NotificationPermissionService
import pl.slaszu.workbreak.domain.schedule.SchedulePermissionService
import javax.inject.Inject

class UserWarningSpecification @Inject constructor(
    private val notificationPermissionService: NotificationPermissionService,
    private val schedulePermissionService: SchedulePermissionService,
    private val clock: Clock
) {
    fun isWarningActive(setting: Setting): UserWarning? {
        if (!notificationPermissionService.hasPermission()
            || !schedulePermissionService.hasPermission()
            || setting.isMuteActive(clock.getNow())
        ) {
            return UserWarning(
                notification = !notificationPermissionService.hasPermission(),
                schedule = !schedulePermissionService.hasPermission(),
                mute = setting.isMuteActive(clock.getNow())
            )
        }
        return null
    }
}

data class UserWarning(
    val notification: Boolean,
    val schedule: Boolean,
    val mute: Boolean,
)

