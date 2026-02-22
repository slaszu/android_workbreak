package pl.slaszu.workbreak.domain

import kotlinx.datetime.toKotlinDayOfWeek
import pl.slaszu.workbreak.domain.model.work.WorkDay
import pl.slaszu.workbreak.domain.model.work.WorkWeek
import pl.slaszu.workbreak.domain.utils.getNextDay
import pl.slaszu.workbreak.domain.utils.getNextDayOfWeek
import pl.slaszu.workbreak.domain.utils.getPrevDayOfWeek
import pl.slaszu.workbreak.domain.utils.resetDay
import java.time.DayOfWeek
import java.time.LocalDateTime

class WorkService {

    fun toWorkPeriodListWithFreeTime(
        workWeek: WorkWeek,
        dateTime: LocalDateTime
    ): List<WorkPeriod> {
        val workPeriodList = toWorkPeriodList(workWeek, dateTime)
        if (workPeriodList.isEmpty()) {
            return emptyList()
        }

        val list = mutableListOf<WorkPeriod>()

        val startDay = getPrevDayOfWeek(dateTime, DayOfWeek.MONDAY)
        val first = workPeriodList.first()

        // add first period
        if (first.startLocaleDateTime > startDay) {
            list.add(
                WorkPeriod(
                    startLocaleDateTime = startDay,
                    endLocaleDateTime = first.startLocaleDateTime.minusNanos(1),
                    type = WorkPeriodType.FREE_TIME
                )
            )
        }

        workPeriodList.forEachIndexed { index, workPeriod ->
            list.add(workPeriod)
            val next = workPeriodList.getOrNull(index + 1)
            if (next == null) {
                // no one left finish
                return@forEachIndexed
            }
            if (workPeriod.endLocaleDateTime.plusNanos(1) == next.startLocaleDateTime) {
                // next period is right after this one
                return@forEachIndexed
            }
            // next period is NOT right after this one
            // add free_time period
            list.add(
                WorkPeriod(
                    startLocaleDateTime = workPeriod.endLocaleDateTime.plusNanos(1),
                    endLocaleDateTime = next.startLocaleDateTime.minusNanos(1),
                    type = WorkPeriodType.FREE_TIME
                )
            )
        }

        val endDay = getNextDayOfWeek(dateTime, DayOfWeek.SUNDAY).plusDays(1).minusNanos(1)
        val last = workPeriodList.last()
        // add last period
        if (last.endLocaleDateTime < endDay) {
            list.add(
                WorkPeriod(
                    startLocaleDateTime = last.endLocaleDateTime.plusNanos(1),
                    endLocaleDateTime = endDay,
                    type = WorkPeriodType.FREE_TIME
                )
            )
        }


        return list.toList()
    }

    fun toWorkPeriodList(workWeek: WorkWeek, dateTime: LocalDateTime): List<WorkPeriod> {
        val list = mutableListOf<WorkPeriod>()

        // get date for monday
        val startDay = getPrevDayOfWeek(dateTime, DayOfWeek.MONDAY)

        var currentDay = startDay
        Days.entries.forEach {
            val workDay = workWeek.getWorkDay(it.dayOfWeek)

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
                    type = WorkPeriodType.WORK
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
                    type = WorkPeriodType.BREAK
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
    this.find { it.startLocaleDateTime <= dateTime && it.endLocaleDateTime >= dateTime }?.let {
        return it
    }

    // rewind to the start of the week
    val rewindDateTime = getPrevDayOfWeek(dateTime, DayOfWeek.MONDAY)

    return this.find { it.startLocaleDateTime <= rewindDateTime && it.endLocaleDateTime >= rewindDateTime }
}

fun List<WorkPeriod>.findNextWorkPeriod(
    dateTime: LocalDateTime,
    type: WorkPeriodType
): WorkPeriod? {

    if (this.isEmpty()) {
        return null
    }

    this.find { it.startLocaleDateTime > dateTime && it.type == type }?.let {
        return it
    }

    // rewind to the start of the week
    val rewindDateTime = getPrevDayOfWeek(dateTime, DayOfWeek.MONDAY)

    return this.find { it.startLocaleDateTime > rewindDateTime && it.type == type }
}


data class WorkPeriod(
    val startLocaleDateTime: LocalDateTime,
    val endLocaleDateTime: LocalDateTime,
    val type: WorkPeriodType
)

enum class WorkPeriodType {
    WORK, BREAK, FREE_TIME
}