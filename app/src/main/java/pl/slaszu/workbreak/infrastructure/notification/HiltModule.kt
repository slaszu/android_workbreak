package pl.slaszu.workbreak.infrastructure.notification

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.slaszu.notification.Configuration
import pl.slaszu.notification.NotificationPermission
import pl.slaszu.workbreak.domain.notification.NotificationPermissionService

@InstallIn(SingletonComponent::class)
@Module
object Providers {

    @Provides
    fun getNotificationPermissionService(
        @ApplicationContext context: Context,
    ): NotificationPermissionService {
        return NotificationPermissionServiceImpl(
            NotificationPermission(
                applicationContext = context,
                configuration = Configuration(
                    channelId = "${context.packageName}.notification.channel",
                    channelName = "Work Break Reminder App",
                    channelDescription = "Work Break Reminder Notification Channel"
                )
            )
        )
    }
}