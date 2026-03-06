package pl.slaszu.workbreak.domain.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import pl.slaszu.workbreak.domain.model.time.Time
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun Int.asMinutesToHoursAndMinutes(): String {
    val time = Time(this)
    return "${time.hours} h ${time.minutes} m"
}

fun LocalDateTime.toEpochMillis(): Long =
    this.toJavaLocalDateTime().atZone(ZoneId.systemDefault()).toEpochSecond().times(1000)

fun LocalDateTime.getDayName(): String =
    this.toJavaLocalDateTime().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US)

fun LocalDateTime.getDateTimeFormatted(): String =
    this.toJavaLocalDateTime().format(
        DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a", Locale.US)
    )