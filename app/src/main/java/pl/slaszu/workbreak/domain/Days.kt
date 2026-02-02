package pl.slaszu.workbreak.domain

import kotlinx.datetime.DayOfWeek
import pl.slaszu.workbreak.R

enum class Days(val dayTranslationKey: Int, val dayOfWeek: DayOfWeek) {
    MONDAY(R.string.monday, DayOfWeek.MONDAY),
    TUESDAY(R.string.tuesday, DayOfWeek.TUESDAY),
    WEDNESDAY(R.string.wednesday, DayOfWeek.WEDNESDAY),
    THURSDAY(R.string.thursday, DayOfWeek.THURSDAY),
    FRIDAY(R.string.friday, DayOfWeek.FRIDAY),
    SATURDAY(R.string.saturday, DayOfWeek.SATURDAY),
    SUNDAY(R.string.sunday, DayOfWeek.SUNDAY);

    companion object {
        fun getForDayOfWeek(dayOfWeek: DayOfWeek): Days {
            return entries.first { it.dayOfWeek == dayOfWeek }
        }

        fun getNextDayOfWeek(dayOfWeek: DayOfWeek): DayOfWeek {
            val index = entries.indexOfFirst { it.dayOfWeek == dayOfWeek }
            return entries[(index + 1) % entries.size].dayOfWeek
        }
    }

}
