package pl.slaszu.workbreak.domain.model.time

data class TimePeriod(
    val start: Time,
    val end: Time,
) {
    val endNextDay: Boolean
        get() = (end < start)

    fun toMinutes(): Int {
        var endTime = end
        if (endNextDay) {
            endTime = endTime + Time(hours = 24, minutes = 0)
        }

        return (endTime.toMinutes() - start.toMinutes())
    }
}
