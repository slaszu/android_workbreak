package pl.slaszu.workbreak.application

import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import pl.slaszu.workbreak.domain.WorkService
import pl.slaszu.workbreak.domain.WorkTypeEnum
import pl.slaszu.workbreak.domain.findNearestBreakWorkPeriod
import pl.slaszu.workbreak.domain.findWorkPeriod
import pl.slaszu.workbreak.domain.model.WorkWeek
import pl.slaszu.workbreak.domain.schedule.BreakScheduleAlarmPeriod
import pl.slaszu.workbreak.domain.schedule.BreakScheduleAlarm
import pl.slaszu.workbreak.domain.schedule.BreakScheduleAlarmType
import pl.slaszu.workbreak.domain.schedule.ScheduleAlarmService
import java.time.LocalDateTime
import javax.inject.Inject

class SetScheduleAlarm @Inject constructor(
    private val scheduleAlarmService: ScheduleAlarmService
) {

    fun setNextScheduleAlarm(
        workWeek: WorkWeek,
        dateTime: LocalDateTime,
        startWorkAlarmFlag: Boolean = false,
        endWorkAlarmFlag: Boolean = false,
    ): LocalDateTime? {
        val workService = WorkService()
        val workPeriodList = workService.toWorkPeriodList(workWeek, dateTime)

        // check if exist period for this datetime
        var breakPeriod = workPeriodList.findWorkPeriod(dateTime)
        var type = BreakScheduleAlarmType.END

        // if not exists then find nearest break period
        if (breakPeriod == null || breakPeriod.type != WorkTypeEnum.BREAK) {
            breakPeriod = workPeriodList.findNearestBreakWorkPeriod(dateTime)
            type = BreakScheduleAlarmType.START
        }

        // if no break period then cancel all alarms
        if (breakPeriod == null) {
            scheduleAlarmService.cancelAllAlarms()
            return null
        }

        // calculate real start and end datetime
        var startDateTime = breakPeriod.startLocaleDateTime
        var endDateTime = breakPeriod.endLocaleDateTime

        if (startDateTime < dateTime && endDateTime < dateTime) {
            startDateTime = startDateTime.plusWeeks(1)
            endDateTime = endDateTime.plusWeeks(1)
        }


        val alarmDateTime = scheduleAlarmService.scheduleBreakAlarm(
            breakData = BreakScheduleAlarm(
                period = BreakScheduleAlarmPeriod(
                    start = startDateTime.toKotlinLocalDateTime(),
                    end = endDateTime.toKotlinLocalDateTime()
                ),
                workWeek = workWeek,
                type = type
            )
        )

        return alarmDateTime?.toJavaLocalDateTime()
    }
}