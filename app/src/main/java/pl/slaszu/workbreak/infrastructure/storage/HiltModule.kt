package pl.slaszu.workbreak.infrastructure.storage

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.slaszu.localstorage.createLocalStorage
import pl.slaszu.workbreak.domain.Clock
import pl.slaszu.workbreak.domain.model.setting.Setting
import pl.slaszu.workbreak.domain.model.work.WorkWeek
import pl.slaszu.workbreak.domain.repository.SettingRepository
import pl.slaszu.workbreak.domain.repository.WorkWeekRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object Providers {

    @Provides
    @Singleton
    fun getWorkWeekRepository(
        @ApplicationContext context: Context,
    ): WorkWeekRepository {
        return LocalStorageWorkWeekRepository(
            localStorage = context.createLocalStorage(
                default = WorkWeek.createWeekActive(),
                filename = "workWeek"
            )
        )
    }

    @Provides
    @Singleton
    fun getSettingRepository(
        @ApplicationContext context: Context,
        clock: Clock
    ): SettingRepository {
        return LocalStorageSettingRepository(
            localStorage = context.createLocalStorage(
                default = Setting(),
                filename = "setting"
            ),
            clock = clock
        )
    }
}