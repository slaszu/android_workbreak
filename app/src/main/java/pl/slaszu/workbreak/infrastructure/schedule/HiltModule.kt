package pl.slaszu.workbreak.infrastructure.schedule

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.slaszu.schedule.SchedulePermission
import pl.slaszu.workbreak.domain.schedule.SchedulePermissionService

@InstallIn(SingletonComponent::class)
@Module
object Providers {

    @Provides
    fun getSchedulePermissionService(
        @ApplicationContext context: Context,
    ): SchedulePermissionService {
        return SchedulePermissionServiceImpl(SchedulePermission(context))
    }
}