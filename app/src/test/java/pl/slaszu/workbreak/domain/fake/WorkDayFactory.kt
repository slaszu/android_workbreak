package pl.slaszu.workbreak.domain.fake

import kotlinx.datetime.DayOfWeek
import pl.slaszu.workbreak.domain.model.work.WorkDay
import pl.slaszu.workbreak.domain.model.work.WorkHours

class WorkDayFactory {
    companion object {
        fun createActiveDay8to16(dayOfWeek: DayOfWeek = DayOfWeek.THURSDAY): WorkDay {
            return WorkDay(
                dayOfWeek = dayOfWeek,
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

        fun createActiveDay18to2(dayOfWeek: DayOfWeek = DayOfWeek.THURSDAY): WorkDay {
            return WorkDay(
                dayOfWeek = dayOfWeek,
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