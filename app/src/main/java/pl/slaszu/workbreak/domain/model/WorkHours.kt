package pl.slaszu.workbreak.domain.model

data class WorkHours(
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
)