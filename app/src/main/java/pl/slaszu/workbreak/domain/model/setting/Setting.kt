package pl.slaszu.workbreak.domain.model.setting

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.Serializable
import java.time.LocalDateTime as LocalDateTimeJava

@Serializable
data class Setting(
    val notificationRequestDisplayed: Boolean = false,
    val scheduleAlarmRequestDisplayed: Boolean = false,
    val showWorkStartReminder: Boolean = true,
    val showWorkEndReminder: Boolean = true,
    val muteUntil: LocalDateTime? = null
) {
    fun isMuteActive(nowDateTime: LocalDateTimeJava): Boolean {
        if (muteUntil == null) return false
        return muteUntil.toJavaLocalDateTime().isAfter(nowDateTime)
    }
}