package pl.slaszu.workbreak.domain.model

data class WorkWeek(
    val workDays: List<WorkDay> = emptyList()
)