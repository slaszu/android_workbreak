package pl.slaszu.workbreak.infrastructure.storage

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.slaszu.localstorage.createLocalStorage
import pl.slaszu.workbreak.domain.model.Setting
import pl.slaszu.workbreak.domain.model.SettingRepository
import pl.slaszu.workbreak.domain.model.WorkWeek
import pl.slaszu.workbreak.domain.model.WorkWeekRepository
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
                default = WorkWeek.create(),
                filename = "workWeek"
            )
        )
    }

    @Provides
    @Singleton
    fun getSettingRepository(
        @ApplicationContext context: Context,
    ): SettingRepository {
        return LocalStorageSettingRepository(
            localStorage = context.createLocalStorage(
                default = Setting(),
                filename = "setting"
            )
        )
    }
}