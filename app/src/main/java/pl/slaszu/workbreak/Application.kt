package pl.slaszu.workbreak

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import pl.slaszu.workbreak.domain.NotificationService
import javax.inject.Inject

@HiltAndroidApp
class Application : Application() {

    @Inject
    lateinit var notificationService: NotificationService

    override fun onCreate() {
        super.onCreate()
        notificationService.registerChannel()
    }
}