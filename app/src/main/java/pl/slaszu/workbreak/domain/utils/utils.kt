package pl.slaszu.workbreak.domain.utils

import pl.slaszu.workbreak.domain.model.time.Time

fun Int.asMinutesToHoursAndMinutes(): String {
    val time = Time(this)
    return "${time.hours} h ${time.minutes} m"
}