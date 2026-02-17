package pl.slaszu.workbreak.infrastructure.storage

import kotlinx.coroutines.flow.Flow
import pl.slaszu.localstorage.LocalStorage
import pl.slaszu.workbreak.domain.model.Setting
import pl.slaszu.workbreak.domain.model.SettingRepository

class LocalStorageSettingRepository(
    private val localStorage: LocalStorage<Setting>
) : SettingRepository {

    override fun get(): Flow<Setting> {
        return localStorage.get()
    }

    override suspend fun persist(setting: Setting) {
        localStorage.save(setting)
    }

}