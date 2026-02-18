package pl.slaszu.workbreak.domain.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pl.slaszu.workbreak.domain.WorkService
import pl.slaszu.workbreak.domain.WorkTypeEnum
import pl.slaszu.workbreak.domain.findWorkPeriod
import pl.slaszu.workbreak.domain.model.WorkWeek
import pl.slaszu.workbreak.domain.utils.getPrevDayOfWeek
import java.time.LocalDateTime
import java.util.stream.Stream
import java.time.DayOfWeek as JavaDayOfWeek


class WorkServiceWeekTest {

    @ParameterizedTest
    @MethodSource("provide")
    fun check(
        workWeek: WorkWeek,
        datetimeModifier: (LocalDateTime) -> LocalDateTime,
        expectedType: WorkTypeEnum?,
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

        val workPeriod = workPeriodList.findWorkPeriod(lookingForDateTime)
        assertEquals(expectedType, workPeriod?.type)
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
                    workWeekActive,
                    { thursday: LocalDateTime -> thursday.plusHours(7).plusMinutes(59) },
                    null,
                ),
                Arguments.of(
                    workWeekInactive,
                    { thursday: LocalDateTime -> thursday.plusHours(7).plusMinutes(59) },
                    null,
                ),
                Arguments.of(
                    workWeekActive,
                    { thursday: LocalDateTime -> thursday.plusHours(8) },
                    WorkTypeEnum.WORK,
                ),
                Arguments.of(
                    workWeekInactive,
                    { thursday: LocalDateTime -> thursday.plusHours(8) },
                    null,
                ),
                Arguments.of(
                    workWeekActive,
                    { thursday: LocalDateTime -> thursday.minusHours(24).plusHours(8) },
                    WorkTypeEnum.WORK,
                ),
                Arguments.of(
                    workWeekActive,
                    { thursday: LocalDateTime ->
                        thursday.minusHours(24).plusHours(8).plusMinutes(44)
                    },
                    WorkTypeEnum.WORK,
                ),
                Arguments.of(
                    workWeekActive,
                    { thursday: LocalDateTime ->
                        thursday.minusHours(24).plusHours(8).plusMinutes(45)
                    },
                    WorkTypeEnum.BREAK,
                )
            )
        }
    }
}

