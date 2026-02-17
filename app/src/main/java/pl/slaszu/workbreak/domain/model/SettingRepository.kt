package pl.slaszu.workbreak.domain.model

import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    fun get(): Flow<Setting>
    suspend fun persist(setting: Setting)
}