package pl.slaszu.workbreak.infrastructure.schedule

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.slaszu.schedule.Schedule
import pl.slaszu.workbreak.domain.ScheduleService

@InstallIn(SingletonComponent::class)
@Module
object Providers {

    @Provides
    fun getScheduleService(
        @ApplicationContext context: Context,
    ): ScheduleService {
        return ScheduleServiceImpl(Schedule(context))
    }
}