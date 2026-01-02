package pl.slaszu.workbreak.domain.model

interface WorkWeekRepository {
    suspend fun get(): WorkWeek
    suspend fun persist(workWeek: WorkWeek)
}