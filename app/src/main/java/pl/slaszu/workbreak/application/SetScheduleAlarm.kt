package pl.slaszu.workbreak.application

import android.util.Log
import kotlinx.datetime.toKotlinLocalDateTime
import pl.slaszu.workbreak.domain.WorkPeriod
import pl.slaszu.workbreak.domain.WorkPeriodType
import pl.slaszu.workbreak.domain.WorkService
import pl.slaszu.workbreak.domain.findNextWorkPeriod
import pl.slaszu.workbreak.domain.findWorkPeriod
import pl.slaszu.workbreak.domain.model.alarm.Alarm
import pl.slaszu.workbreak.domain.model.work.WorkWeek
import pl.slaszu.workbreak.domain.schedule.ScheduleAlarmService
import pl.slaszu.workbreak.domain.utils.tikPlus
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
    ): Alarm? {
        val workService = WorkService()
        val workPeriodList = workService.toWorkPeriodListWithFreeTime(workWeek, dateTime)

        // check if some period is durating now
        if (workPeriodList.isEmpty()) {
            scheduleAlarmService.cancelAllAlarms()
            return null
        }

        // actual work period
        var workPeriod = workPeriodList.findWorkPeriod(dateTime)
        // or next work period in case of weeks break (two work periods with free_time type in row)
        if (workPeriod?.type == WorkPeriodType.FREE_TIME) {
            workPeriod = workPeriodList.findWorkPeriod(workPeriod.endLocaleDateTime.tikPlus())
        }

        val nextWorkPeriod =
            workPeriodList.findWorkPeriod(workPeriod!!.endLocaleDateTime.tikPlus())

        val nextBreakPeriod = workPeriodList.findNextWorkPeriod(
            dateTime = dateTime,
            type = WorkPeriodType.BREAK
        )

        val alarm = createAlarmData(
            workPeriod = workPeriod,
            nextWorkPeriod = nextWorkPeriod!!,
            nextBreakPeriod = nextBreakPeriod,
            startWorkAlarmFlag = startWorkAlarmFlag,
            endWorkAlarmFlag = endWorkAlarmFlag
        )

        if (alarm == null) {
            scheduleAlarmService.cancelAllAlarms()
            return null
        }

        scheduleAlarmService.scheduleBreakAlarm(alarm)

        return alarm
    }

    private fun createAlarmData(
        workPeriod: WorkPeriod,
        nextWorkPeriod: WorkPeriod,
        nextBreakPeriod: WorkPeriod?,
        startWorkAlarmFlag: Boolean,
        endWorkAlarmFlag: Boolean
    ): Alarm? {
        when (workPeriod.type) {
            WorkPeriodType.WORK -> {
                if (nextWorkPeriod.type == WorkPeriodType.FREE_TIME) {
                    if (endWorkAlarmFlag) {
                        // set alert for AlarmDataType.WORK_END
                        return Alarm.WorkEnd(
                            alarmDateTime = workPeriod.endLocaleDateTime.toKotlinLocalDateTime()
                        )
                    }
                    if (startWorkAlarmFlag) {
                        // set alert for AlarmDataType.WORK_START
                        return Alarm.WorkStart(
                            alarmDateTime = nextWorkPeriod.endLocaleDateTime.plusNanos(1)
                                .toKotlinLocalDateTime()
                        )
                    }
                }

                // set alert for AlarmDataType.BREAK_START
                if (nextBreakPeriod != null) {
                    return Alarm.BreakStart(
                        alarmDateTime = nextBreakPeriod.startLocaleDateTime.toKotlinLocalDateTime()
                    )
                }
            }

            WorkPeriodType.BREAK -> {
                if (nextWorkPeriod.type == WorkPeriodType.FREE_TIME) {
                    if (endWorkAlarmFlag) {
                        // set alert for AlarmDataType.WORK_END
                        return Alarm.WorkEnd(
                            alarmDateTime = workPeriod.endLocaleDateTime.toKotlinLocalDateTime(),
                            duringBreak = true
                        )
                    }
                }

                // set alert for AlarmDataType.BREAK_END
                return Alarm.BreakEnd(
                    alarmDateTime = workPeriod.endLocaleDateTime.toKotlinLocalDateTime()
                )
            }

            WorkPeriodType.FREE_TIME -> {
                if (startWorkAlarmFlag) {
                    // set alert for AlarmDataType.WORK_START
                    return Alarm.WorkStart(
                        alarmDateTime = workPeriod.endLocaleDateTime.plusNanos(1)
                            .toKotlinLocalDateTime()
                    )
                }

                // set alert for AlarmDataType.BREAK_START
                if (nextBreakPeriod != null) {
                    return Alarm.BreakStart(
                        alarmDateTime = nextBreakPeriod.startLocaleDateTime.toKotlinLocalDateTime()
                    )
                }
            }
        }

        return null
    }
}