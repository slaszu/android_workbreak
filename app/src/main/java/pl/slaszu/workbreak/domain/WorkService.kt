package pl.slaszu.workbreak.domain

import kotlinx.datetime.toKotlinDayOfWeek
import pl.slaszu.workbreak.domain.model.WorkDay
import pl.slaszu.workbreak.domain.model.WorkWeek
import java.time.DayOfWeek
import java.time.LocalDateTime

class WorkService {

    fun getPrevDayOfWeek(startDay: LocalDateTime, dayOfWeek: DayOfWeek): LocalDateTime {
        var currentDay = startDay
        while (currentDay.dayOfWeek != dayOfWeek) {
            currentDay = getPrevDay(currentDay)
        }
        return currentDay
    }


    fun toWorkPeriodList(workWeek: WorkWeek, dateTime: LocalDateTime): List<WorkPeriod> {
        val list = mutableListOf<WorkPeriod>()

        // get date for monday
        var currentDay = getPrevDayOfWeek(dateTime, DayOfWeek.MONDAY)

        Days.entries.forEach {
            val workDay = workWeek.getWorkDay(it.dayOfWeek)
            if (!workDay.active) {
                currentDay = getNextDay(currentDay)
                return@forEach
            }

            list.addAll(toWorkPeriodList(workDay, currentDay))

            currentDay = getNextDay(currentDay)
        }

        return list.toList()
    }

    fun toWorkPeriodList(workDay: WorkDay, dateTime: LocalDateTime): List<WorkPeriod> {
        if (workDay.dayOfWeek != dateTime.dayOfWeek.toKotlinDayOfWeek()) {
            throw IllegalArgumentException("Day of week does not match")
        }

        if (!workDay.active) {
            return emptyList()
        }

        val timePeriod = workDay.workHours.timePeriod

        val currentDay = resetDay(dateTime)

        var startDate = currentDay.plusHours(timePeriod.start.hours.toLong())
            .plusMinutes(timePeriod.start.minutes.toLong())

        val endDate = currentDay.plusHours(timePeriod.end.hours.toLong())
            .plusMinutes(timePeriod.end.minutes.toLong())
            .also { localeDateTime ->
                if (timePeriod.endNextDay) {
                    localeDateTime.plusDays(1)
                }
            }

        val list = mutableListOf<WorkPeriod>()

        while (startDate < endDate) {

            // for work
            var breakFlag = false;
            var nextDate = startDate.plusMinutes(workDay.breakEveryXMinutes.toLong())
            if (nextDate > endDate) {
                nextDate = endDate
                breakFlag = true
            }

            list.add(
                WorkPeriod(
                    startLocaleDateTime = startDate,
                    endLocaleDateTime = nextDate,
                    type = WorkTypeEnum.WORK
                )
            )

            startDate = nextDate

            if (breakFlag) {
                break
            }

            // for break
            breakFlag = false
            nextDate = nextDate.plusMinutes(workDay.breakDurationMinutes.toLong())
            if (nextDate > endDate) {
                nextDate = endDate
                breakFlag = true
            }

            list.add(
                WorkPeriod(
                    startLocaleDateTime = startDate,
                    endLocaleDateTime = nextDate,
                    type = WorkTypeEnum.BREAK
                )
            )

            startDate = nextDate

            if (breakFlag) {
                break
            }
        }

        return list.toList()
    }

    private fun resetDay(day: LocalDateTime): LocalDateTime {
        return day.withHour(0).withMinute(0).withSecond(0).withNano(0)
    }

    private fun getPrevDay(day: LocalDateTime): LocalDateTime {
        return resetDay(day.minusDays(1))
    }

    private fun getNextDay(day: LocalDateTime): LocalDateTime {
        return resetDay(day.plusDays(1))
    }

}

data class WorkPeriod(
    val startLocaleDateTime: LocalDateTime,
    val endLocaleDateTime: LocalDateTime,
    val type: WorkTypeEnum
)

enum class WorkTypeEnum {
    WORK, BREAK
}