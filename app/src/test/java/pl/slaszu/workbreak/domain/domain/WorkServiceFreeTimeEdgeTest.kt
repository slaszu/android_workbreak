package pl.slaszu.workbreak.domain.domain

import kotlinx.datetime.DayOfWeek.FRIDAY
import kotlinx.datetime.DayOfWeek.THURSDAY
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import pl.slaszu.workbreak.domain.WorkPeriodType
import pl.slaszu.workbreak.domain.WorkService
import pl.slaszu.workbreak.domain.findWorkPeriod
import pl.slaszu.workbreak.domain.model.work.WorkWeek
import pl.slaszu.workbreak.domain.utils.getPrevDayOfWeek
import java.time.DayOfWeek
import java.time.LocalDateTime

class WorkServiceFreeTimeEdgeTest {
    @Test
    fun hasFreeAtBeginningAndEnd() {

        // arrange
        val workWeek = WorkWeek.createWeekInactive()
        val workWeekOnlyThursday = workWeek.copy(
            workDays = workWeek.workDays.map {
                if (it.dayOfWeek == THURSDAY) {
                    it.copy(active = true)
                } else {
                    it
                }
            }
        )

        println("workWeekOnlyThursday: $workWeekOnlyThursday")

        // act
        val startDay = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)
        val workService = WorkService()
        val workPeriodWithFree = workService.toWorkPeriodListWithFreeTime(
            workWeek = workWeekOnlyThursday,
            startDay = startDay
        )

        // assert
        workPeriodWithFree.first().let {
            println(it)
            Assertions.assertTrue(it.startLocaleDateTime == startDay)
            Assertions.assertTrue(
                it.endLocaleDateTime == startDay.plusHours(8).minusNanos(1)
            )
            Assertions.assertTrue(it.type == WorkPeriodType.FREE_TIME)
        }

        workPeriodWithFree.last().let {
            println(it)
            Assertions.assertTrue(
                it.startLocaleDateTime == startDay.plusDays(7).plusHours(16)
            )
            Assertions.assertTrue(
                it.endLocaleDateTime == startDay.plusDays(8).minusNanos(1)
            )
            Assertions.assertTrue(it.type == WorkPeriodType.FREE_TIME)
        }
    }

    @Test
    fun hasFreeBetweenThursdayAndFriday() {

        // arrange
        val workWeek = WorkWeek.createWeekInactive()
        val workWeekOnlyThursday = workWeek.copy(
            workDays = workWeek.workDays.map {
                if (it.dayOfWeek == THURSDAY || it.dayOfWeek == FRIDAY) {
                    it.copy(active = true)
                } else {
                    it
                }
            }
        )

        println("workWeekOnlyThursday: $workWeekOnlyThursday")

        // act
        val startDay = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)
        val workService = WorkService()
        val workPeriodWithFree = workService.toWorkPeriodListWithFreeTime(
            workWeek = workWeekOnlyThursday,
            startDay = startDay
        )

        // assert
        workPeriodWithFree.findWorkPeriod(startDay.plusHours(16))!!.let {
            println(it)
            Assertions.assertTrue(it.startLocaleDateTime == startDay.plusHours(16))
            Assertions.assertTrue(
                it.endLocaleDateTime == startDay.plusDays(1).plusHours(8).minusNanos(1)
            )
            Assertions.assertTrue(it.type == WorkPeriodType.FREE_TIME)
        }

    }
}