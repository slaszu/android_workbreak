package pl.slaszu.workbreak.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkHours(
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
)