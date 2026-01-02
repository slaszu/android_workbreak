package pl.slaszu.workbreak.infrastructure.notification

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.slaszu.notification.Configuration
import pl.slaszu.notification.Notification
import pl.slaszu.workbreak.domain.NotificationService

@InstallIn(SingletonComponent::class)
@Module
object Providers {

    @Provides
    fun getNotificationService(
        @ApplicationContext context: Context,
    ): NotificationService {
        return NotificationServiceImpl(
            Notification(
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