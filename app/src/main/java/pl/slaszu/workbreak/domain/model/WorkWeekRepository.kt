package pl.slaszu.workbreak.domain.model

import kotlinx.coroutines.flow.Flow

interface WorkWeekRepository {
    fun get(): Flow<WorkWeek>
    suspend fun persist(workWeek: WorkWeek)
}