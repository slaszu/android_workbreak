package pl.slaszu.workbreak.domain.model

import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable

@Serializable
data class WorkDay(
    val dayOfWeek: DayOfWeek,
    val workHours: WorkHours,
    val breakEveryXMinutes: Int,
    val breakDurationMinutes: Int,
)
