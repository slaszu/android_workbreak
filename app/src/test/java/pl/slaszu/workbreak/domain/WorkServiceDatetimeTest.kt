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


class WorkServiceDatetimeTest {

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
                    { dateTime: LocalDateTime -> dateTime.plusHours(7).plusMinutes(59) },
                    null,
                    null
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
                        breakDurationMinutes = 5,
                        breakEveryXMinutes = 55,
                        active = true
                    ),
                    { dateTime: LocalDateTime -> dateTime.plusHours(8) },
                    WorkTypeEnum.WORK,
                    1
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
                        breakDurationMinutes = 5,
                        breakEveryXMinutes = 55,
                        active = true
                    ),
                    { dateTime: LocalDateTime -> dateTime.plusHours(8).plusMinutes(55) },
                    WorkTypeEnum.BREAK,
                    2
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
                        breakDurationMinutes = 5,
                        breakEveryXMinutes = 55,
                        active = true
                    ),
                    { dateTime: LocalDateTime -> dateTime.plusHours(8).plusMinutes(59) },
                    WorkTypeEnum.BREAK,
                    2
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
                        breakDurationMinutes = 5,
                        breakEveryXMinutes = 55,
                        active = true
                    ),
                    { dateTime: LocalDateTime -> dateTime.plusHours(9) },
                    WorkTypeEnum.WORK,
                    3
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
                        breakDurationMinutes = 5,
                        breakEveryXMinutes = 55,
                        active = true
                    ),
                    { dateTime: LocalDateTime -> dateTime.plusHours(9).plusMinutes(3) },
                    WorkTypeEnum.WORK,
                    3
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
                        breakDurationMinutes = 5,
                        breakEveryXMinutes = 55,
                        active = true
                    ),
                    { dateTime: LocalDateTime -> dateTime.plusHours(15).plusMinutes(59) },
                    WorkTypeEnum.BREAK,
                    16
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
                        breakDurationMinutes = 5,
                        breakEveryXMinutes = 55,
                        active = true
                    ),
                    { dateTime: LocalDateTime -> dateTime.plusHours(16) },
                    null,
                    null
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
                        breakEveryXMinutes = 55,
                        active = true
                    ),
                    { dateTime: LocalDateTime -> dateTime.plusHours(18) },
                    WorkTypeEnum.WORK,
                    1
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
                        breakEveryXMinutes = 55,
                        active = true
                    ),
                    { dateTime: LocalDateTime -> dateTime.plusHours(25).plusMinutes(59) },
                    WorkTypeEnum.BREAK,
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
                        breakDurationMinutes = 5,
                        breakEveryXMinutes = 55,
                        active = true
                    ),
                    { dateTime: LocalDateTime -> dateTime.plusHours(26) },
                    null,
                    null
                ),
            )
        }
    }
}

