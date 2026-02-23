package pl.slaszu.workbreak.domain.model.alarm

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Alarm() {
    abstract val alarmDateTime: LocalDateTime

    @Serializable
    @SerialName("WorkStart")
    data class WorkStart(override var alarmDateTime: LocalDateTime) : Alarm()

    @Serializable
    @SerialName("WorkEnd")
    data class WorkEnd(
        override var alarmDateTime: LocalDateTime,
        val duringBreak: Boolean = false
    ) : Alarm()

    @Serializable
    @SerialName("BreakStart")
    data class BreakStart(override var alarmDateTime: LocalDateTime) : Alarm()

    @Serializable
    @SerialName("BreakEnd")
    data class BreakEnd(override var alarmDateTime: LocalDateTime) : Alarm()
}