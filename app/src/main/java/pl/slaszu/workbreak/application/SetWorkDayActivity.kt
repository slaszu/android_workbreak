package pl.slaszu.workbreak.application

import kotlinx.datetime.DayOfWeek
import pl.slaszu.workbreak.domain.model.WorkWeek

class SetWorkDayActivity {
    fun setWorkDay(workWeek: WorkWeek, dayOfWeek: DayOfWeek, active: Boolean): WorkWeek {
        val days = workWeek.workDays
        val day = days.first { it.dayOfWeek == dayOfWeek }
        val newDay = day.copy(active = active)
        val newDays = days.map { if (it.dayOfWeek == dayOfWeek) newDay else it }
        return WorkWeek(newDays)
    }
}