package pl.slaszu.workbreak.domain.domain

import kotlinx.datetime.DayOfWeek
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pl.slaszu.workbreak.application.SetWorkDay
import pl.slaszu.workbreak.domain.WorkPeriodType
import pl.slaszu.workbreak.domain.WorkService
import pl.slaszu.workbreak.domain.findNextWorkPeriod
import pl.slaszu.workbreak.domain.model.work.WorkWeek
import pl.slaszu.workbreak.domain.utils.getPrevDayOfWeek
import java.time.LocalDateTime
import java.util.stream.Stream
import java.time.DayOfWeek as JavaDayOfWeek


class WorkServiceNextBreakTest {

    @ParameterizedTest
    @MethodSource("provide")
    fun check(
        workWeek: WorkWeek,
        datetimeModifier: (LocalDateTime) -> LocalDateTime,
        startLocalDateTimeModifier: (LocalDateTime) -> LocalDateTime?,
    ) {

        val workService = WorkService()

        val thursday = getPrevDayOfWeek(LocalDateTime.now(), JavaDayOfWeek.THURSDAY)

        val workPeriodList = workService.toWorkPeriodList(
            workWeek = workWeek,
            dateTime = thursday
        )

        println(workPeriodList)

        val lookingForDateTime = datetimeModifier(thursday)

        println(lookingForDateTime)

        val workPeriod = workPeriodList.findNextWorkPeriod(lookingForDateTime, WorkPeriodType.BREAK)
        assertEquals(startLocalDateTimeModifier(thursday), workPeriod?.startLocaleDateTime)
    }

    companion object {
        @JvmStatic
        fun provide(): Stream<Arguments> {
            var workWeek = WorkWeek.create()
            val setWorkDay = SetWorkDay()

            // active thursday
            val thursday = workWeek.getWorkDay(DayOfWeek.THURSDAY)
            workWeek = setWorkDay.setWorkDay(workWeek, thursday.copy(active = true))

            // active thursday
            val friday = workWeek.getWorkDay(DayOfWeek.FRIDAY)
            workWeek = setWorkDay.setWorkDay(workWeek, friday.copy(active = true))

            return Stream.of(
                Arguments.of(
                    workWeek,
                    { thursday: LocalDateTime -> thursday.plusHours(7).plusMinutes(59) },
                    { thursday: LocalDateTime -> thursday.plusHours(8).plusMinutes(45) }
                ),
                Arguments.of(
                    workWeek,
                    { thursday: LocalDateTime -> thursday.plusHours(8).plusMinutes(44) },
                    { thursday: LocalDateTime -> thursday.plusHours(8).plusMinutes(45) }
                ),
                Arguments.of(
                    workWeek,
                    { thursday: LocalDateTime -> thursday.plusHours(8).plusMinutes(45) },
                    { thursday: LocalDateTime -> thursday.plusHours(9).plusMinutes(45) }
                ),
                Arguments.of(
                    workWeek,
                    { thursday: LocalDateTime -> thursday.plusHours(8).plusMinutes(46) },
                    { thursday: LocalDateTime -> thursday.plusHours(9).plusMinutes(45) }
                ),
                Arguments.of(
                    workWeek,
                    { thursday: LocalDateTime -> thursday.plusHours(9).plusMinutes(0) },
                    { thursday: LocalDateTime -> thursday.plusHours(9).plusMinutes(45) }
                ),
                Arguments.of(
                    workWeek,
                    { thursday: LocalDateTime ->
                        thursday.plusDays(1).plusHours(9).plusMinutes(0)
                    },
                    { thursday: LocalDateTime ->
                        thursday.plusDays(1).plusHours(9).plusMinutes(45)
                    }
                ),
                Arguments.of(
                    workWeek,
                    { thursday: LocalDateTime ->
                        thursday.plusDays(1).plusHours(9).plusMinutes(45)
                    },
                    { thursday: LocalDateTime ->
                        thursday.plusDays(1).plusHours(10).plusMinutes(45)
                    }
                ),
                Arguments.of(
                    workWeek,
                    { thursday: LocalDateTime ->
                        thursday.plusDays(2).plusHours(9).plusMinutes(45)
                    },
                    { thursday: LocalDateTime -> thursday.plusHours(8).plusMinutes(45) }
                ),
                Arguments.of(
                    WorkWeek.create(),
                    { thursday: LocalDateTime ->
                        thursday.plusDays(2).plusHours(9).plusMinutes(45)
                    },
                    { thursday: LocalDateTime -> null }
                )
            )
        }
    }
}

