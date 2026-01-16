package pl.slaszu.workbreak.domain.model

import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable
import pl.slaszu.workbreak.domain.Days

@Serializable
data class WorkWeek(
    val workDays: List<WorkDay>
) {
    fun getWorkDay(dayOfWeek: DayOfWeek): WorkDay {
        return workDays.first { it.dayOfWeek == dayOfWeek }
    }

    fun isValid(): Boolean {
        Days.entries.forEach {
            try {
                getWorkDay(it.dayOfWeek)
            } catch (e: NoSuchElementException) {
                return false
            }
        }
        return true
    }

    companion object {
        fun create(): WorkWeek {
            return WorkWeek(
                workDays = List(7) {
                    WorkDay.create(Days.entries[it].dayOfWeek)
                }
            )
        }
    }
}