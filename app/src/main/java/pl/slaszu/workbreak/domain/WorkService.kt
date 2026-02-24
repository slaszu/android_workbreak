package pl.slaszu.workbreak.domain

import kotlinx.datetime.toKotlinDayOfWeek
import pl.slaszu.workbreak.domain.model.work.WorkDay
import pl.slaszu.workbreak.domain.model.work.WorkWeek
import pl.slaszu.workbreak.domain.utils.resetDay
import java.time.LocalDateTime

class WorkService {

    fun toWorkPeriodListWithFreeTime(
        workWeek: WorkWeek,
        startDay: LocalDateTime
    ): List<WorkPeriod> {
        val workPeriodList = toWorkPeriodList(workWeek, startDay)
        if (workPeriodList.isEmpty()) {
            return emptyList()
        }

        val startDayReset = resetDay(startDay)


        val list = mutableListOf<WorkPeriod>()

        val first = workPeriodList.first()

        // add first period
        if (first.startLocaleDateTime > startDayReset) {
            list.add(
                WorkPeriod(
                    startLocaleDateTime = startDayReset,
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

        val endDay = startDayReset.plusDays(8).minusNanos(1)
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

    fun toWorkPeriodList(workWeek: WorkWeek, startDay: LocalDateTime): List<WorkPeriod> {
        val list = mutableListOf<WorkPeriod>()

        var currentDay = startDay
        for (i in 0..7) { // 0..7 double first day at the end
            currentDay = startDay.plusDays(i.toLong())
            val workDay = workWeek.getWorkDay(currentDay.dayOfWeek.toKotlinDayOfWeek())
            list.addAll(toWorkPeriodList(workDay, currentDay))
        }

        return list.toList()
    }

    fun toWorkPeriodList(workDay: WorkDay, dateTime: LocalDateTime): List<WorkPeriod> {

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
    return this.find { it.startLocaleDateTime <= dateTime && it.endLocaleDateTime >= dateTime }
}

fun List<WorkPeriod>.findNextWorkPeriod(
    dateTime: LocalDateTime,
    type: WorkPeriodType
): WorkPeriod? {
    return this.find { it.startLocaleDateTime > dateTime && it.type == type }
}


data class WorkPeriod(
    val startLocaleDateTime: LocalDateTime,
    val endLocaleDateTime: LocalDateTime,
    val type: WorkPeriodType
)

enum class WorkPeriodType {
    WORK, BREAK, FREE_TIME
}