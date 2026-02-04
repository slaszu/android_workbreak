package pl.slaszu.workbreak.domain

import kotlinx.datetime.DayOfWeek
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pl.slaszu.workbreak.domain.model.WorkDay
import pl.slaszu.workbreak.domain.model.WorkHours
import java.time.LocalDateTime
import java.util.stream.Stream
import java.time.DayOfWeek as JavaDayOfWeek


class WorkServicePeriodCountTest {

    @ParameterizedTest
    @MethodSource("provide")
    fun check(workDay: WorkDay, expectedSize: Int) {

        val workService = WorkService()
        val workPeriodList = workService.toWorkPeriodList(
            workDay = workDay,
            dateTime = workService.getPrevDayOfWeek(LocalDateTime.now(), JavaDayOfWeek.THURSDAY)
        )

        println(workPeriodList)

        assertEquals(expectedSize, workPeriodList.size)
    }

    companion object {
        @JvmStatic
        fun provide(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    WorkDay(
                        dayOfWeek = DayOfWeek.THURSDAY,
                        workHours = WorkHours(
                            startHour = 8,
                            startMinute = 0,
                            endHour = 16,
                            endMinute = 0
                        ),
                        breakDurationMinutes = 5,
                        breakEveryXMinutes = 55,
                        active = true
                    ),
                    16
                ),
                Arguments.of(
                    WorkDay(
                        dayOfWeek = DayOfWeek.THURSDAY,
                        workHours = WorkHours(
                            startHour = 18,
                            startMinute = 0,
                            endHour = 2,
                            endMinute = 0
                        ),
                        breakDurationMinutes = 15,
                        breakEveryXMinutes = 45,
                        active = true
                    ),
                    16
                ),
                Arguments.of(
                    WorkDay(
                        dayOfWeek = DayOfWeek.THURSDAY,
                        workHours = WorkHours(
                            startHour = 18,
                            startMinute = 0,
                            endHour = 2,
                            endMinute = 0
                        ),
                        breakDurationMinutes = 10,
                        breakEveryXMinutes = 60,
                        active = true
                    ),
                    13
                ),
                Arguments.of(
                    WorkDay(
                        dayOfWeek = DayOfWeek.THURSDAY,
                        workHours = WorkHours(
                            startHour = 18,
                            startMinute = 0,
                            endHour = 2,
                            endMinute = 0
                        ),
                        breakDurationMinutes = 5,
                        breakEveryXMinutes = 60,
                        active = true
                    ),
                    15
                ),

                Arguments.of(
                    WorkDay(
                        dayOfWeek = DayOfWeek.THURSDAY,
                        workHours = WorkHours(
                            startHour = 8,
                            startMinute = 0,
                            endHour = 16,
                            endMinute = 0
                        ),
                        breakDurationMinutes = 10,
                        breakEveryXMinutes = 50,
                        active = false
                    ),
                    0
                )
            )
        }
    }

}