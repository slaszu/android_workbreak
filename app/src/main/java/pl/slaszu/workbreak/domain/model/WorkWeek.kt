package pl.slaszu.workbreak.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkWeek(
    val workDays: List<WorkDay> = emptyList()
)