package pl.slaszu.workbreak.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.slaszu.workbreak.domain.model.setting.Setting

interface SettingRepository {
    fun get(): Flow<Setting>
    suspend fun persist(setting: Setting)
}