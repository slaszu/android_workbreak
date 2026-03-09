package pl.slaszu.workbreak.application

import jakarta.inject.Inject
import pl.slaszu.workbreak.domain.WorkPeriodType
import pl.slaszu.workbreak.domain.WorkService
import pl.slaszu.workbreak.domain.findNextWorkPeriod
import pl.slaszu.workbreak.domain.findWorkPeriod
import pl.slaszu.workbreak.domain.model.work.WorkWeek
import java.time.LocalDateTime

class MuteForToday @Inject constructor() {

    fun getMuteUntilNextFreeTime(workWeek: WorkWeek, nowTime: LocalDateTime): LocalDateTime? {
        val workPeriodList = WorkService().toWorkPeriodListWithFreeTime(workWeek, nowTime)

        val actualWorkPeriod = workPeriodList.findWorkPeriod(nowTime)
            ?: return null

        if (actualWorkPeriod.type == WorkPeriodType.FREE_TIME) {
            return actualWorkPeriod.endLocaleDateTime.plusNanos(1)
        }

        val nextFreeTime =
            workPeriodList.findNextWorkPeriod(nowTime, WorkPeriodType.FREE_TIME)
                ?: return null

        return nextFreeTime.endLocaleDateTime.plusNanos(1)
    }

}