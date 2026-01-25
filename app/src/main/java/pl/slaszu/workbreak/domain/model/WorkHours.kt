package pl.slaszu.workbreak.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkHours(
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
) {
    val startTime: String
        get() = "$startHour:${startMinute.toString().padStart(2, '0')}"
    val endTime: String
        get() = "$endHour:${endMinute.toString().padStart(2, '0')}"

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