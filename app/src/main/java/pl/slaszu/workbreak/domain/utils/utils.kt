package pl.slaszu.workbreak.domain.utils

fun Int.asMinutesToHoursAndMinutes(): String {
    val hours = this.div(60)
    val minutes = this.rem(60)
    return "$hours:$minutes"
}