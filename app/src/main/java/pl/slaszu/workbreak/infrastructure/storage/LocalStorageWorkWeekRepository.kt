package pl.slaszu.workbreak.infrastructure.storage

import kotlinx.coroutines.flow.first
import pl.slaszu.localstorage.LocalStorage
import pl.slaszu.workbreak.domain.model.WorkWeek
import pl.slaszu.workbreak.domain.model.WorkWeekRepository

class LocalStorageWorkWeekRepository(
    private val localStorage: LocalStorage<WorkWeek>
) : WorkWeekRepository {
    override suspend fun get(): WorkWeek {
        return localStorage.get().first()
    }

    override suspend fun persist(workWeek: WorkWeek) {
        localStorage.save(workWeek)
    }
}