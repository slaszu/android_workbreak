package pl.slaszu.workbreak.domain.utils

import java.time.DayOfWeek
import java.time.LocalDateTime

fun getPrevDayOfWeek(startDay: LocalDateTime, dayOfWeek: DayOfWeek): LocalDateTime {
    var currentDay = startDay
    while (currentDay.dayOfWeek != dayOfWeek) {
        currentDay = getPrevDay(currentDay)
    }
    return resetDay(currentDay)
}

fun getNextDayOfWeek(startDay: LocalDateTime, dayOfWeek: DayOfWeek): LocalDateTime {
    var currentDay = startDay
    while (currentDay.dayOfWeek != dayOfWeek) {
        currentDay = getNextDay(currentDay)
    }
    return resetDay(currentDay)
}

fun resetDay(day: LocalDateTime): LocalDateTime {
    return day.withHour(0).withMinute(0).withSecond(0).withNano(0)
}

fun getPrevDay(day: LocalDateTime): LocalDateTime {
    return resetDay(day.minusDays(1))
}

fun getNextDay(day: LocalDateTime): LocalDateTime {
    return resetDay(day.plusDays(1))
}

fun LocalDateTime.tikPlus(): LocalDateTime {
    val tik = this.plusNanos(1)
    if (this.dayOfWeek == DayOfWeek.SUNDAY && tik.dayOfWeek == DayOfWeek.MONDAY) {
        return tik.minusWeeks(1)
    }
    return tik
}