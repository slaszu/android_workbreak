package pl.slaszu.workbreak.domain.model.setting

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Setting(
    val notificationRequestDisplayed: Boolean = false,
    val scheduleAlarmRequestDisplayed: Boolean = false,
    val showWorkStartReminder: Boolean = true,
    val showWorkEndReminder: Boolean = true,
    val muteUntil: LocalDateTime? = null
)