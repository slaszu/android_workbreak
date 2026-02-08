package pl.slaszu.workbreak.domain

import kotlinx.datetime.toKotlinDayOfWeek
import pl.slaszu.workbreak.domain.model.WorkDay
import pl.slaszu.workbreak.domain.model.WorkWeek
import pl.slaszu.workbreak.domain.utils.getNextDay
import pl.slaszu.workbreak.domain.utils.getPrevDayOfWeek
import pl.slaszu.workbreak.domain.utils.resetDay
import java.time.DayOfWeek
import java.time.LocalDateTime

class WorkService {

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

        var endDate = currentDay.plusHours(timePeriod.end.hours.toLong())
            .plusMinutes(timePeriod.end.minutes.toLong())

        if (timePeriod.endNextDay) {
            endDate = endDate.plusDays(1)
        }

        val list = mutableListOf<WorkPeriod>()

        while (startDate < endDate) {

            // for work
            var breakFlag = false
            var nextDate = startDate.plusMinutes(workDay.breakEveryXMinutes.toLong())
            if (nextDate >= endDate) {
                nextDate = endDate
                breakFlag = true
            }

            list.add(
                WorkPeriod(
                    startLocaleDateTime = startDate,
                    endLocaleDateTime = nextDate.minusNanos(1),
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
            if (nextDate >= endDate) {
                nextDate = endDate
                breakFlag = true
            }

            list.add(
                WorkPeriod(
                    startLocaleDateTime = startDate,
                    endLocaleDateTime = nextDate.minusNanos(1),
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
}

fun List<WorkPeriod>.findWorkPeriod(dateTime: LocalDateTime): WorkPeriod? {
    return this.find { it.startLocaleDateTime <= dateTime && it.endLocaleDateTime >= dateTime }
}

fun List<WorkPeriod>.findNearestBreakWorkPeriod(dateTime: LocalDateTime): WorkPeriod? {

    if (this.isEmpty()) {
        return null
    }

    this.find { it.startLocaleDateTime > dateTime && it.type == WorkTypeEnum.BREAK }?.let {
        return it
    }

    // rewind to the start of the week
    val rewindDateTime = getPrevDayOfWeek(dateTime, DayOfWeek.MONDAY)

    return this.find { it.startLocaleDateTime > rewindDateTime && it.type == WorkTypeEnum.BREAK }
}


data class WorkPeriod(
    val startLocaleDateTime: LocalDateTime,
    val endLocaleDateTime: LocalDateTime,
    val type: WorkTypeEnum
)

enum class WorkTypeEnum {
    WORK, BREAK
}