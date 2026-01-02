package pl.slaszu.workbreak.domain.model

import kotlinx.datetime.DayOfWeek

data class WorkDay(
    val dayOfWeek: DayOfWeek,
    val workHours: WorkHours,
    val breakEveryXMinutes: Int,
    val breakDurationMinutes: Int,
)
