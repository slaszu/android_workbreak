package pl.slaszu.workbreak.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkHours(
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
) {
    companion object {
        fun create(): WorkHours {
            return WorkHours(
                startHour = 8,
                startMinute = 0,
                endHour = 16,
                endMinute = 0,
            )
        }
    }
}