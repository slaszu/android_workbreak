package pl.slaszu.workbreak.ui

import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable

@Serializable
object ListRouting

@Serializable
object SettingRoute

@Serializable
data class DayEditRoute(
    val day: DayOfWeek
)