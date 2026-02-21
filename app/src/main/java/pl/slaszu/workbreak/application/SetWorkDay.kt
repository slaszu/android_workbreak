package pl.slaszu.workbreak.application

import jakarta.inject.Inject
import pl.slaszu.workbreak.domain.model.work.WorkDay
import pl.slaszu.workbreak.domain.model.work.WorkWeek

class SetWorkDay @Inject constructor() {
    fun setWorkDay(workWeek: WorkWeek, workDay: WorkDay): WorkWeek {
        val days = workWeek.workDays
        val newDays = days.map { if (it.dayOfWeek == workDay.dayOfWeek) workDay else it }
        return WorkWeek(newDays)
    }
}