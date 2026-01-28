package pl.slaszu.workbreak.domain.model

import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable

@Serializable
data class WorkDay(
    val dayOfWeek: DayOfWeek,
    val workHours: WorkHours,
    val breakEveryXMinutes: Int,
    val breakDurationMinutes: Int,
    val active: Boolean = false,
) {

    companion object {
        fun create(dayOfWeek: DayOfWeek): WorkDay {
            return WorkDay(
                dayOfWeek = dayOfWeek,
                workHours = WorkHours.create(),
                breakEveryXMinutes = 45,
                breakDurationMinutes = 15,
            )
        }
    }
}

fun WorkDay.getBreaksQuantity(): Int {
    val durationMinutes = workHours.getDurationMinutes()
    return durationMinutes / (breakEveryXMinutes + breakDurationMinutes)
}

fun WorkDay.getWorkDurationMinutes(): Int {
    val durationMinutes = workHours.getDurationMinutes()
    val breaksQuantity = getBreaksQuantity()
    return durationMinutes - breaksQuantity * (breakDurationMinutes)
}