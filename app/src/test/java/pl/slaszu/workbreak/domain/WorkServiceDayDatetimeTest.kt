package pl.slaszu.workbreak.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pl.slaszu.workbreak.domain.fake.WorkDayFactory
import pl.slaszu.workbreak.domain.model.WorkDay
import java.time.LocalDateTime
import java.util.stream.Stream
import java.time.DayOfWeek as JavaDayOfWeek


class WorkServiceDayDatetimeTest {

    @ParameterizedTest
    @MethodSource("provide")
    fun check(
        workDay: WorkDay,
        datetimeModifier: (LocalDateTime) -> LocalDateTime,
        expectedType: WorkTypeEnum?,
        inWhichPeriod: Int?
    ) {

        val workService = WorkService()

        val startDatetime =
            workService.getPrevDayOfWeek(LocalDateTime.now(), JavaDayOfWeek.THURSDAY)

        val workPeriodList = workService.toWorkPeriodList(
            workDay = workDay,
            dateTime = startDatetime
        )

        println(workPeriodList)

        val lookingForDateTime = datetimeModifier(startDatetime)

        println(lookingForDateTime)

        val workPeriod = workPeriodList.findWorkPeriod(lookingForDateTime)
        assertEquals(expectedType, workPeriod?.type)

        if (expectedType != null) {
            val witchWorkPeriod = workPeriodList.indexOf(workPeriod) + 1
            assertEquals(inWhichPeriod, witchWorkPeriod)
        }
    }

    companion object {
        @JvmStatic
        fun provide(): Stream<Arguments> {

            val day = WorkDayFactory.createActiveDay8to16()
            val day2 = WorkDayFactory.createActiveDay18to2()

            return Stream.of(
                Arguments.of(
                    day,
                    { dateTime: LocalDateTime -> dateTime.plusHours(7).plusMinutes(59) },
                    null,
                    null
                ),
                Arguments.of(
                    day,
                    { dateTime: LocalDateTime -> dateTime.plusHours(8) },
                    WorkTypeEnum.WORK,
                    1
                ),
                Arguments.of(
                    day,
                    { dateTime: LocalDateTime -> dateTime.plusHours(8).plusMinutes(55) },
                    WorkTypeEnum.BREAK,
                    2
                ),
                Arguments.of(
                    day,
                    { dateTime: LocalDateTime -> dateTime.plusHours(8).plusMinutes(59) },
                    WorkTypeEnum.BREAK,
                    2
                ),
                Arguments.of(
                    day,
                    { dateTime: LocalDateTime -> dateTime.plusHours(9) },
                    WorkTypeEnum.WORK,
                    3
                ),
                Arguments.of(
                    day,
                    { dateTime: LocalDateTime -> dateTime.plusHours(9).plusMinutes(3) },
                    WorkTypeEnum.WORK,
                    3
                ),
                Arguments.of(
                    day,
                    { dateTime: LocalDateTime -> dateTime.plusHours(15).plusMinutes(59) },
                    WorkTypeEnum.BREAK,
                    16
                ),
                Arguments.of(
                    day,
                    { dateTime: LocalDateTime -> dateTime.plusHours(16) },
                    null,
                    null
                ),

                Arguments.of(
                    day2,
                    { dateTime: LocalDateTime -> dateTime.plusHours(18) },
                    WorkTypeEnum.WORK,
                    1
                ),
                Arguments.of(
                    day2,
                    { dateTime: LocalDateTime -> dateTime.plusHours(25).plusMinutes(59) },
                    WorkTypeEnum.BREAK,
                    16
                ),
                Arguments.of(
                    day2,
                    { dateTime: LocalDateTime -> dateTime.plusHours(26) },
                    null,
                    null
                ),
            )
        }
    }
}

