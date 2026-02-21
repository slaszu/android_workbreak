package pl.slaszu.workbreak.domain.model.alarm

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
sealed class Alarm(open val alarmDateTime: LocalDateTime) {
    data class WorkStart(override val alarmDateTime: LocalDateTime) : Alarm(alarmDateTime)
    data class WorkEnd(override val alarmDateTime: LocalDateTime, val duringBreak: Boolean = false): Alarm(alarmDateTime)
    data class BreakStart(override val alarmDateTime: LocalDateTime): Alarm(alarmDateTime)
    data class BreakEnd(override val alarmDateTime: LocalDateTime): Alarm(alarmDateTime)
}