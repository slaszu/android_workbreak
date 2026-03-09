package pl.slaszu.workbreak.infrastructure.storage

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.slaszu.localstorage.LocalStorage
import pl.slaszu.workbreak.domain.Clock
import pl.slaszu.workbreak.domain.model.setting.Setting
import pl.slaszu.workbreak.domain.repository.SettingRepository

class LocalStorageSettingRepository(
    private val localStorage: LocalStorage<Setting>,
    private val clock: Clock
) : SettingRepository {

    override fun get(): Flow<Setting> {
        return localStorage.get().map {
            if (!it.isMuteActive(clock.getNow())) {
                it.copy(
                    muteUntil = null
                )
            } else {
                it
            }
        }
    }

    override suspend fun persist(setting: Setting) {
        localStorage.save(setting)
    }

}