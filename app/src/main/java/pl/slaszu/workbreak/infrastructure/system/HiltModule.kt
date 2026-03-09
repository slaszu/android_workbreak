package pl.slaszu.workbreak.infrastructure.system

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.slaszu.workbreak.domain.Clock

@Module
@InstallIn(SingletonComponent::class)
abstract class HiltModule {
    @Binds
    abstract fun bindClock(
        clock: ClockImpl
    ): Clock
}