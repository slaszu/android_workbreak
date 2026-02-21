package pl.slaszu.workbreak.domain.model.setting

import kotlinx.serialization.Serializable

@Serializable
data class Setting(
    val notificationRequestDisplayed: Boolean = false,
    val scheduleAlarmRequestDisplayed: Boolean = false,
    val showWorkStartReminder: Boolean = true,
    val showWorkEndReminder: Boolean = true
)