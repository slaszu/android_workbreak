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
    fun isWarningActive(setting: Setting): Boolean {
        return !notificationPermissionService.hasPermission()
                || !schedulePermissionService.hasPermission()
                || setting.isMuteActive(clock.getNow())
    }
}

