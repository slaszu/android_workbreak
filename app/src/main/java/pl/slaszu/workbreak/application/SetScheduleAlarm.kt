package pl.slaszu.workbreak.application

import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import pl.slaszu.workbreak.domain.WorkService
import pl.slaszu.workbreak.domain.WorkTypeEnum
import pl.slaszu.workbreak.domain.findNearestBreakWorkPeriod
import pl.slaszu.workbreak.domain.findWorkPeriod
import pl.slaszu.workbreak.domain.model.WorkWeek
import pl.slaszu.workbreak.domain.schedule.BreakPeriod
import pl.slaszu.workbreak.domain.schedule.BreakScheduleAlarm
import pl.slaszu.workbreak.domain.schedule.BreakScheduleAlarmType
import pl.slaszu.workbreak.domain.schedule.ScheduleAlarmService
import java.time.LocalDateTime
import javax.inject.Inject

class SetScheduleAlarm @Inject constructor(
    private val scheduleAlarmService: ScheduleAlarmService
) {

    fun setNextBreakScheduleAlarm(workWeek: WorkWeek, dateTime: LocalDateTime): LocalDateTime? {
        val workService = WorkService()
        val workPeriodList = workService.toWorkPeriodList(workWeek, dateTime)

        var breakPeriod = workPeriodList.findWorkPeriod(dateTime)
        if (breakPeriod == null || breakPeriod.type != WorkTypeEnum.BREAK) {
            breakPeriod = workPeriodList.findNearestBreakWorkPeriod(dateTime)
        }

        if (breakPeriod == null) return null

        val alarmDateTime = scheduleAlarmService.scheduleBreakAlarm(
            breakData = BreakScheduleAlarm(
                period = BreakPeriod(
                    start = breakPeriod.startLocaleDateTime.toKotlinLocalDateTime(),
                    end = breakPeriod.endLocaleDateTime.toKotlinLocalDateTime()
                ),
                workDay = workWeek.getWorkDay(breakPeriod.startLocaleDateTime.toKotlinLocalDateTime().dayOfWeek),
                type = BreakScheduleAlarmType.START
            )
        )

        return alarmDateTime?.toJavaLocalDateTime()
    }
}