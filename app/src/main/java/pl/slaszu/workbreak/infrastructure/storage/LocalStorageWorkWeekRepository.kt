package pl.slaszu.workbreak.infrastructure.storage

import kotlinx.coroutines.flow.Flow
import pl.slaszu.localstorage.LocalStorage
import pl.slaszu.workbreak.domain.model.WorkWeek
import pl.slaszu.workbreak.domain.model.WorkWeekRepository

class LocalStorageWorkWeekRepository(
    private val localStorage: LocalStorage<WorkWeek>
) : WorkWeekRepository {
    override fun get(): Flow<WorkWeek> {
        return localStorage.get()
    }

    override suspend fun persist(workWeek: WorkWeek) {
        localStorage.save(workWeek)
    }
}