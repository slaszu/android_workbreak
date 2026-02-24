package pl.slaszu.workbreak.domain.model.work

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
        fun createWeekInactive(): WorkWeek {
            return WorkWeek(
                workDays = List(7) {
                    WorkDay.create(Days.entries[it].dayOfWeek)
                }
            )
        }

        fun createWeekActive(): WorkWeek {
            val workWeek = createWeekInactive()
            return workWeek.copy(
                workDays = workWeek.workDays.map {
                    if (it.dayOfWeek in DayOfWeek.MONDAY..DayOfWeek.FRIDAY) {
                        it.copy(active = true)
                    } else {
                        it
                    }
                }
            )
        }
    }
}