package pl.slaszu.workbreak.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.slaszu.workbreak.domain.model.work.WorkWeek

interface WorkWeekRepository {
    fun get(): Flow<WorkWeek>
    suspend fun persist(workWeek: WorkWeek)
}