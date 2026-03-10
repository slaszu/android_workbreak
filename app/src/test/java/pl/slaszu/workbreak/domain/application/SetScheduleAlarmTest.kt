package pl.slaszu.workbreak.domain.application

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.datetime.toJavaLocalDateTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pl.slaszu.workbreak.application.SetScheduleAlarm
import pl.slaszu.workbreak.domain.model.alarm.Alarm
import pl.slaszu.workbreak.domain.model.work.WorkWeek
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
            workWeek = WorkWeek.createWeekInactive(),
            nowTime = thursday
        )

        // assert
        Assertions.assertNull(res)
        verify { scheduleAlarmService.cancelAllAlarms() }
    }

    @Test
    fun firstBreakBegin() {
        // arrange
        val workWeekActive = WorkWeek.createWeekActive()
        val thursday = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)
        every { scheduleAlarmService.scheduleBreakAlarm(any(Alarm::class)) } returns Unit

        // act
        val res = setScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeekActive,
            nowTime = thursday
        )

        Assertions.assertEquals(
            thursday.plusHours(8).plusMinutes(45),
            res!!.alarmDateTime.toJavaLocalDateTime()
        )
        Assertions.assertEquals(Alarm.BreakStart::class, res::class)

        // assert
        verify {
            scheduleAlarmService.scheduleBreakAlarm(res)
        }
    }

    @Test
    fun firstBreakEnd() {
        // arrange
        val workWeekActive = WorkWeek.createWeekActive()
        val thursday = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)
        every { scheduleAlarmService.scheduleBreakAlarm(any(Alarm::class)) } returns Unit

        // act
        val res = setScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeekActive,
            nowTime = thursday.plusHours(8).plusMinutes(55)
        )

        Assertions.assertEquals(
            thursday.plusHours(9).minusNanos(1),
            res!!.alarmDateTime.toJavaLocalDateTime()
        )
        Assertions.assertEquals(Alarm.BreakEnd::class, res::class)

        // assert
        verify {
            scheduleAlarmService.scheduleBreakAlarm(res)
        }
    }

    @Test
    fun workStartForNowBefore() {
        // arrange
        val workWeekActive = WorkWeek.createWeekActive()
        val thursday = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)
        every { scheduleAlarmService.scheduleBreakAlarm(any(Alarm::class)) } returns Unit

        // act
        val res = setScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeekActive,
            nowTime = thursday.plusHours(8).minusNanos(1),
            startWorkAlarmFlag = true
        )

        Assertions.assertEquals(thursday.plusHours(8), res!!.alarmDateTime.toJavaLocalDateTime())
        Assertions.assertEquals(Alarm.WorkStart::class, res::class)

        // assert
        verify {
            scheduleAlarmService.scheduleBreakAlarm(res)
        }
    }

    @Test
    fun workStartForNowExactly() {
        // arrange
        val workWeekActive = WorkWeek.createWeekActive()
        val thursday = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)
        every { scheduleAlarmService.scheduleBreakAlarm(any(Alarm::class)) } returns Unit

        // act
        val res = setScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeekActive,
            nowTime = thursday.plusHours(8),
            startWorkAlarmFlag = true
        )

        Assertions.assertEquals(
            thursday.plusHours(8).plusMinutes(45),
            res!!.alarmDateTime.toJavaLocalDateTime()
        )
        Assertions.assertEquals(Alarm.BreakStart::class, res::class)

        // assert
        verify {
            scheduleAlarmService.scheduleBreakAlarm(res)
        }
    }

    @Test
    fun workStartForNowAfter() {
        // arrange
        val workWeekActive = WorkWeek.createWeekActive()
        val thursday = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)
        every { scheduleAlarmService.scheduleBreakAlarm(any(Alarm::class)) } returns Unit

        // act
        val res = setScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeekActive,
            nowTime = thursday.plusHours(8).plusNanos(1),
            startWorkAlarmFlag = true
        )

        Assertions.assertEquals(
            thursday.plusHours(8).plusMinutes(45),
            res!!.alarmDateTime.toJavaLocalDateTime()
        )
        Assertions.assertEquals(Alarm.BreakStart::class, res::class)

        // assert
        verify {
            scheduleAlarmService.scheduleBreakAlarm(res)
        }
    }


    @Test
    fun workEndForNowBefore() {
        // arrange
        val workWeekActive = WorkWeek.createWeekActive()
        val thursday = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)
        every { scheduleAlarmService.scheduleBreakAlarm(any(Alarm::class)) } returns Unit

        // act
        val res = setScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeekActive,
            nowTime = thursday.plusHours(16).minusNanos(1),
            endWorkAlarmFlag = true
        )

        Assertions.assertEquals(
            thursday.plusHours(16).minusNanos(1),
            res!!.alarmDateTime.toJavaLocalDateTime()
        )
        Assertions.assertEquals(Alarm.WorkEnd::class, res::class)

        // assert
        verify {
            scheduleAlarmService.scheduleBreakAlarm(res)
        }
    }

    @Test
    fun workEndForNowExactly() {
        // arrange
        val workWeekActive = WorkWeek.createWeekActive()
        val thursday = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)
        every { scheduleAlarmService.scheduleBreakAlarm(any(Alarm::class)) } returns Unit

        // act
        val res = setScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeekActive,
            nowTime = thursday.plusHours(16),
            endWorkAlarmFlag = true
        )

        Assertions.assertEquals(
            thursday.plusDays(1).plusHours(8).plusMinutes(45),
            res!!.alarmDateTime.toJavaLocalDateTime()
        )
        Assertions.assertEquals(Alarm.BreakStart::class, res::class)

        // assert
        verify {
            scheduleAlarmService.scheduleBreakAlarm(res)
        }
    }

    @Test
    fun workEndForNowAfter() {
        // arrange
        val workWeekActive = WorkWeek.createWeekActive()
        val thursday = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)
        every { scheduleAlarmService.scheduleBreakAlarm(any(Alarm::class)) } returns Unit

        // act
        val res = setScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeekActive,
            nowTime = thursday.plusHours(16).plusNanos(1),
            endWorkAlarmFlag = true
        )

        Assertions.assertEquals(
            thursday.plusDays(1).plusHours(8).plusMinutes(45),
            res!!.alarmDateTime.toJavaLocalDateTime()
        )
        Assertions.assertEquals(Alarm.BreakStart::class, res::class)

        // assert
        verify {
            scheduleAlarmService.scheduleBreakAlarm(res)
        }
    }




    @Test
    fun muteUntilForNowBefore() {
        // arrange
        val workWeekActive = WorkWeek.createWeekActive()
        val thursday = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)
        every { scheduleAlarmService.scheduleBreakAlarm(any(Alarm::class)) } returns Unit

        // act
        val res = setScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeekActive,
            nowTime = thursday.plusDays(1).plusHours(8).minusNanos(1),
            startWorkAlarmFlag = true,
            muteUntil = thursday.plusDays(1).plusHours(8)
        )

        println(res)

        Assertions.assertEquals(thursday.plusDays(1).plusHours(8), res!!.alarmDateTime.toJavaLocalDateTime())
        Assertions.assertEquals(Alarm.WorkStart::class, res::class)

        // assert
        verify {
            scheduleAlarmService.scheduleBreakAlarm(res)
        }
    }

    @Test
    fun muteUntilForNowExactly() {
        // arrange
        val workWeekActive = WorkWeek.createWeekActive()
        val thursday = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)
        every { scheduleAlarmService.scheduleBreakAlarm(any(Alarm::class)) } returns Unit

        // act
        val res = setScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeekActive,
            nowTime = thursday.plusDays(1).plusHours(8),
            startWorkAlarmFlag = true,
            muteUntil = thursday.plusDays(1).plusHours(8)
        )

        println(res)

        Assertions.assertEquals(thursday.plusDays(1).plusHours(8).plusMinutes(45), res!!.alarmDateTime.toJavaLocalDateTime())
        Assertions.assertEquals(Alarm.BreakStart::class, res::class)

        // assert
        verify {
            scheduleAlarmService.scheduleBreakAlarm(res)
        }
    }

    @Test
    fun muteUntilForNowAfter() {
        // arrange
        val workWeekActive = WorkWeek.createWeekActive()
        val thursday = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)
        every { scheduleAlarmService.scheduleBreakAlarm(any(Alarm::class)) } returns Unit

        // act
        val res = setScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeekActive,
            nowTime = thursday.plusDays(1).plusHours(8).plusNanos(1),
            startWorkAlarmFlag = true,
            muteUntil = thursday.plusDays(1).plusHours(8)
        )

        println(res)

        Assertions.assertEquals(thursday.plusDays(1).plusHours(8).plusMinutes(45), res!!.alarmDateTime.toJavaLocalDateTime())
        Assertions.assertEquals(Alarm.BreakStart::class, res::class)

        // assert
        verify {
            scheduleAlarmService.scheduleBreakAlarm(res)
        }
    }
}
