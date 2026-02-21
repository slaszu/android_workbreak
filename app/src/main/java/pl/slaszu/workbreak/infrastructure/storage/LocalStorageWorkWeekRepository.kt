package pl.slaszu.workbreak.infrastructure.storage

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.slaszu.localstorage.LocalStorage
import pl.slaszu.workbreak.domain.model.work.WorkWeek
import pl.slaszu.workbreak.domain.repository.WorkWeekRepository

class LocalStorageWorkWeekRepository(
    private val localStorage: LocalStorage<WorkWeek>
) : WorkWeekRepository {
    override fun get(): Flow<WorkWeek> {
        return localStorage.get().map {
            if (!it.isValid()) {
                WorkWeek.create()
            }
            it
        }
    }

    override suspend fun persist(workWeek: WorkWeek) {
        localStorage.save(workWeek)
    }
}