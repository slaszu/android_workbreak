package pl.slaszu.workbreak.infrastructure.storage

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.slaszu.localstorage.createLocalStorage
import pl.slaszu.workbreak.domain.model.WorkWeek
import pl.slaszu.workbreak.domain.model.WorkWeekRepository

@InstallIn(SingletonComponent::class)
@Module
object Providers {

    @Provides
    fun getWorkWeekRepository(
        @ApplicationContext context: Context,
    ): WorkWeekRepository {
        return LocalStorageWorkWeekRepository(
            localStorage = context.createLocalStorage(
                default = WorkWeek(),
                filename = "workWeek"
            )
        )
    }
}