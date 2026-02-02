package pl.slaszu.workbreak.domain.model.time

data class Time(
    val hours: Int,
    val minutes: Int,
) {
    constructor(minutes: Int) : this(minutes / 60, minutes % 60)

    fun toMinutes(): Int {
        return hours * 60 + minutes
    }

    operator fun plus(other: Time): Time {
        return Time(hours + other.hours, minutes + other.minutes)
    }

    override fun toString(): String {
        return "$hours:${minutes.toString().padStart(2, '0')}"
    }

    operator fun compareTo(other: Time): Int {
        return if (toMinutes() > other.toMinutes()) 1 else -1
    }
}