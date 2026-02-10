package pl.slaszu.workbreak.domain.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import pl.slaszu.workbreak.domain.model.time.Time
import java.time.ZoneId

fun Int.asMinutesToHoursAndMinutes(): String {
    val time = Time(this)
    return "${time.hours} h ${time.minutes} m"
}

fun LocalDateTime.toEpochMillis(): Long =
    this.toJavaLocalDateTime().atZone(ZoneId.systemDefault()).toEpochSecond().times(1000)