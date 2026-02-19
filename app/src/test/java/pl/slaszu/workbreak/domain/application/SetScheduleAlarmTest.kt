package pl.slaszu.workbreak.domain.application

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.datetime.toKotlinLocalDateTime
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pl.slaszu.workbreak.application.SetScheduleAlarm
import pl.slaszu.workbreak.domain.model.WorkWeek
import pl.slaszu.workbreak.domain.schedule.AlarmData
import pl.slaszu.workbreak.domain.schedule.AlarmDataPeriod
import pl.slaszu.workbreak.domain.schedule.AlarmDataType
import pl.slaszu.workbreak.domain.schedule.ScheduleAlarmService
import pl.slaszu.workbreak.domain.utils.getPrevDayOfWeek
import java.time.DayOfWeek
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class SetScheduleAlarmTest {

    @MockK
    lateinit var scheduleAlarmService: ScheduleAlarmService

    @InjectMockKs
    lateinit var setScheduleAlarm: SetScheduleAlarm

    @Test
    fun noWorkPeriodsFound() {
        // arrange
        every { scheduleAlarmService.cancelAllAlarms() } returns Unit
        val thursday = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)

        // act
        val res = setScheduleAlarm.setNextScheduleAlarm(
            workWeek = WorkWeek.create(),
            dateTime = thursday
        )

        // assert
        assertNull(res)
        verify { scheduleAlarmService.cancelAllAlarms() }
    }

    @Test
    fun firstBreakBegin() {
        // arrange
        val workWeekInactive = WorkWeek.create()
        val workWeekActive = workWeekInactive.copy(
            workDays = workWeekInactive.workDays.map {
                it.copy(active = true)
            }
        )
        val thursday = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)
        every { scheduleAlarmService.scheduleBreakAlarm(any()) } returns thursday.toKotlinLocalDateTime()

        // act
        val res = setScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeekActive,
            dateTime = thursday
        )

        // assert
        verify {
            scheduleAlarmService.scheduleBreakAlarm(
                AlarmData(
                    workWeek = workWeekActive,
                    period = AlarmDataPeriod(
                        start = thursday.plusHours(8).plusMinutes(45).toKotlinLocalDateTime(),
                        end = thursday.plusHours(9).minusNanos(1).toKotlinLocalDateTime()
                    ),
                    type = AlarmDataType.BREAK_START
                )
            )
        }
    }

    @Test
    fun firstBreakEnd() {
        // arrange
        val workWeekInactive = WorkWeek.create()
        val workWeekActive = workWeekInactive.copy(
            workDays = workWeekInactive.workDays.map {
                it.copy(active = true)
            }
        )
        val thursday = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)
        every { scheduleAlarmService.scheduleBreakAlarm(any()) } returns thursday.toKotlinLocalDateTime()

        // act
        val res = setScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeekActive,
            dateTime = thursday.plusHours(8).plusMinutes(45)
        )

        // assert
        verify {
            scheduleAlarmService.scheduleBreakAlarm(
                AlarmData(
                    workWeek = workWeekActive,
                    period = AlarmDataPeriod(
                        start = thursday.plusHours(8).plusMinutes(45).toKotlinLocalDateTime(),
                        end = thursday.plusHours(9).minusNanos(1).toKotlinLocalDateTime()
                    ),
                    type = AlarmDataType.BREAK_END
                )
            )
        }
    }

    //@Test
    fun firstWorkBegin() {
        // arrange
        val workWeekInactive = WorkWeek.create()
        val workWeekActive = workWeekInactive.copy(
            workDays = workWeekInactive.workDays.map {
                it.copy(active = true)
            }
        )
        val thursday = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)
        every { scheduleAlarmService.scheduleBreakAlarm(any()) } returns thursday.toKotlinLocalDateTime()

        // act
        val res = setScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeekActive,
            dateTime = thursday,
            startWorkAlarmFlag = true
        )

        // assert
        verify {
            scheduleAlarmService.scheduleBreakAlarm(
                AlarmData(
                    workWeek = workWeekActive,
                    period = AlarmDataPeriod(
                        start = thursday.plusHours(8).toKotlinLocalDateTime(),
                        end = thursday.plusHours(8).plusMinutes(45).minusNanos(1)
                            .toKotlinLocalDateTime()
                    ),
                    type = AlarmDataType.WORK_START
                )
            )
        }
    }

}