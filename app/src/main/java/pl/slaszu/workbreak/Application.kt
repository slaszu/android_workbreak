package pl.slaszu.workbreak

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import pl.slaszu.workbreak.domain.notification.NotificationPermissionService
import javax.inject.Inject

@HiltAndroidApp
class Application : Application() {

    @Inject
    lateinit var notificationPermissionService: NotificationPermissionService

    override fun onCreate() {
        super.onCreate()
        notificationPermissionService.registerChannel()
    }
}