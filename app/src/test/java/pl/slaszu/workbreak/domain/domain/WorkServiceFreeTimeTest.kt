package pl.slaszu.workbreak.domain.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pl.slaszu.workbreak.domain.WorkPeriod
import pl.slaszu.workbreak.domain.WorkService
import pl.slaszu.workbreak.domain.WorkPeriodType
import pl.slaszu.workbreak.domain.findWorkPeriod
import pl.slaszu.workbreak.domain.model.work.WorkWeek
import pl.slaszu.workbreak.domain.utils.getPrevDayOfWeek
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.stream.Stream

class WorkServiceFreeTimeTest {
    @ParameterizedTest
    @MethodSource("provide")
    fun check(
        workWeek: WorkWeek,
        datetimeModifier: (LocalDateTime) -> LocalDateTime,
        expectedWorkPeriod: (LocalDateTime) -> WorkPeriod?,
    ) {

        val workService = WorkService()

        val thursday = getPrevDayOfWeek(LocalDateTime.now(), DayOfWeek.THURSDAY)

        val workPeriodList = workService.toWorkPeriodListWithFreeTime(
            workWeek = workWeek,
            dateTime = thursday
        )

        println(workPeriodList)

        val lookingForDateTime = datetimeModifier(thursday)
        val expectedWorkPeriod = expectedWorkPeriod(thursday)

        println(lookingForDateTime)

        val workPeriod = workPeriodList.findWorkPeriod(lookingForDateTime)

        assertEquals(expectedWorkPeriod, workPeriod)
    }

    companion object {
        @JvmStatic
        fun provide(): Stream<Arguments> {
            val workWeekInactive = WorkWeek.create()
            val workWeekActive = workWeekInactive.copy(
                workDays = workWeekInactive.workDays.map {
                    it.copy(active = true)
                }
            )
            return Stream.of(
                Arguments.of(
                    workWeekInactive,
                    { thursday: LocalDateTime -> thursday.plusHours(7).plusMinutes(59) },
                    { thursday: LocalDateTime -> null },
                ),
                Arguments.of(
                    workWeekInactive,
                    { thursday: LocalDateTime -> thursday.plusHours(8) },
                    { thursday: LocalDateTime -> null },
                ),
                Arguments.of(
                    workWeekActive,
                    { thursday: LocalDateTime -> thursday.plusHours(7).plusMinutes(59) },
                    { thursday: LocalDateTime ->
                        WorkPeriod(
                            startLocaleDateTime = thursday.minusDays(1).plusHours(16),
                            endLocaleDateTime = thursday.plusHours(8).minusNanos(1),
                            type = WorkPeriodType.FREE_TIME
                        )
                    }
                ),
                Arguments.of(
                    workWeekActive,
                    { thursday: LocalDateTime -> thursday.plusHours(8) },
                    { thursday: LocalDateTime ->
                        WorkPeriod(
                            startLocaleDateTime = thursday.plusHours(8),
                            endLocaleDateTime = thursday.plusHours(8).plusMinutes(45).minusNanos(1),
                            type = WorkPeriodType.WORK
                        )
                    }
                ),
                Arguments.of(
                    workWeekActive,
                    { thursday: LocalDateTime -> thursday.plusHours(8).plusMinutes(45) },
                    { thursday: LocalDateTime ->
                        WorkPeriod(
                            startLocaleDateTime = thursday.plusHours(8).plusMinutes(45),
                            endLocaleDateTime = thursday.plusHours(9).minusNanos(1),
                            type = WorkPeriodType.BREAK
                        )
                    }
                ),
                Arguments.of(
                    workWeekActive,
                    { thursday: LocalDateTime -> thursday.plusHours(15).plusMinutes(55) },
                    { thursday: LocalDateTime ->
                        WorkPeriod(
                            startLocaleDateTime = thursday.plusHours(15).plusMinutes(45),
                            endLocaleDateTime = thursday.plusHours(16).minusNanos(1),
                            type = WorkPeriodType.BREAK
                        )
                    }
                ),
                Arguments.of(
                    workWeekActive,
                    { thursday: LocalDateTime -> thursday.plusHours(16) },
                    { thursday: LocalDateTime ->
                        WorkPeriod(
                            startLocaleDateTime = thursday.plusHours(16),
                            endLocaleDateTime = thursday.plusDays(1).plusHours(8).minusNanos(1),
                            type = WorkPeriodType.FREE_TIME
                        )
                    }
                ),
            )
        }
    }
}