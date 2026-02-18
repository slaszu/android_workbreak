package pl.slaszu.workbreak.domain.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pl.slaszu.workbreak.domain.WorkService
import pl.slaszu.workbreak.domain.fake.WorkDayFactory
import pl.slaszu.workbreak.domain.model.WorkDay
import pl.slaszu.workbreak.domain.utils.getPrevDayOfWeek
import java.time.LocalDateTime
import java.util.stream.Stream
import java.time.DayOfWeek as JavaDayOfWeek


class WorkServiceDayPeriodCountTest {

    @ParameterizedTest
    @MethodSource("provide")
    fun check(workDay: WorkDay, expectedSize: Int) {

        val workService = WorkService()
        val workPeriodList = workService.toWorkPeriodList(
            workDay = workDay,
            dateTime = getPrevDayOfWeek(LocalDateTime.now(), JavaDayOfWeek.THURSDAY)
        )

        println(workPeriodList)

        assertEquals(expectedSize, workPeriodList.size)
    }

    companion object {
        @JvmStatic
        fun provide(): Stream<Arguments> {

            val day = WorkDayFactory.createActiveDay8to16()
            val day2 = WorkDayFactory.createActiveDay18to2()

            return Stream.of(
                Arguments.of(
                    day,
                    16
                ),
                Arguments.of(
                    day2.copy(
                        breakDurationMinutes = 15,
                        breakEveryXMinutes = 45
                    ),
                    16
                ),
                Arguments.of(
                    day2.copy(
                        breakDurationMinutes = 10,
                        breakEveryXMinutes = 60
                    ),
                    13
                ),
                Arguments.of(
                    day2.copy(
                        breakDurationMinutes = 5,
                        breakEveryXMinutes = 60
                    ),
                    15
                ),

                Arguments.of(
                    day.copy(active = false),
                    0
                )
            )
        }
    }

}