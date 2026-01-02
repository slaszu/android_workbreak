package pl.slaszu.localstorage

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow

class LocalStorage<T>(
    private val dataStore: DataStore<T>
) {
    fun get(): Flow<T> {
        return dataStore.data
    }

    suspend fun save(storage: T) {
        dataStore.updateData { storage }
    }
}
