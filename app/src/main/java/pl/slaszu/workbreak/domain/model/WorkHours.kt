package pl.slaszu.workbreak.domain.model

import kotlinx.serialization.Serializable
import pl.slaszu.workbreak.domain.model.time.Time
import pl.slaszu.workbreak.domain.model.time.TimePeriod

@Serializable
data class WorkHours(
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
) {
    val startTime: Time
        get() = Time(hours = startHour, minutes = startMinute)
    val endTime: Time
        get() = Time(hours = endHour, minutes = endMinute)

    val timePeriod: TimePeriod
        get() = TimePeriod(start = startTime, end = endTime)

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