package pl.slaszu.workbreak.application

import jakarta.inject.Inject
import pl.slaszu.workbreak.domain.model.work.WorkDay
import pl.slaszu.workbreak.domain.model.work.WorkWeek

class CopyWorkDay @Inject constructor() {
    fun copyWorkDay(workWeek: WorkWeek, copyDay: WorkDay, daysSelected: List<WorkDay>): WorkWeek {
        val days = workWeek.workDays.map {
            if (daysSelected.contains(it)) {
                copyDay.copy(
                    dayOfWeek = it.dayOfWeek,
                )
            } else {
                it
            }
        }

        return WorkWeek(days)
    }
}