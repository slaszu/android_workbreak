package pl.slaszu.workbreak.domain.fake

import kotlinx.datetime.DayOfWeek
import pl.slaszu.workbreak.domain.model.WorkDay
import pl.slaszu.workbreak.domain.model.WorkHours

class WorkDayFactory {
    companion object {
        fun createActiveDay8to16(): WorkDay {
            return WorkDay(
                dayOfWeek = DayOfWeek.THURSDAY,
                workHours = WorkHours(
                    startHour = 8,
                    startMinute = 0,
                    endHour = 16,
                    endMinute = 0
                ),
                breakDurationMinutes = 5,
                breakEveryXMinutes = 55,
                active = true
            )
        }
        fun createActiveDay18to2(): WorkDay {
            return WorkDay(
                dayOfWeek = DayOfWeek.THURSDAY,
                workHours = WorkHours(
                    startHour = 18,
                    startMinute = 0,
                    endHour = 2,
                    endMinute = 0
                ),
                breakDurationMinutes = 5,
                breakEveryXMinutes = 55,
                active = true
            )
        }
    }
}