package pl.slaszu.workbreak.domain.application

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pl.slaszu.workbreak.application.MuteForToday
import pl.slaszu.workbreak.domain.WorkService
import pl.slaszu.workbreak.domain.model.work.WorkWeek
import pl.slaszu.workbreak.domain.utils.getPrevDayOfWeek
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.stream.Stream

class MuteForTodayTest {

    @ParameterizedTest
    @MethodSource("provide")
    fun getCorrectWorkPeriod(
        datetimeModifier: (LocalDateTime) -> LocalDateTime,
        expectedDatetime: (LocalDateTime) -> LocalDateTime
    ) {
        // arrange
        val workWeek = WorkWeek.createWeekActive()
        val nowDate = LocalDateTime.now()
        val thursday = getPrevDayOfWeek(nowDate, DayOfWeek.THURSDAY)
        val workPeriodList = WorkService().toWorkPeriodListWithFreeTime(
            workWeek = workWeek,
            startDay = thursday
        )

        println(workPeriodList)

        val datetimeModifier = datetimeModifier(thursday)
        val expectedDatetime = expectedDatetime(thursday)

        // act
        val useCase = MuteForToday()
        val muteDay = useCase.getMuteUntilNextFreeTime(workWeek, datetimeModifier)
        println(datetimeModifier)
        println(muteDay)

        // assert
        Assertions.assertEquals(
            expectedDatetime,
            muteDay
        )
    }

    companion object {
        @JvmStatic
        fun provide(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    { thursday: LocalDateTime -> thursday },
                    { thursday: LocalDateTime -> thursday.plusHours(8) }
                ),
                Arguments.of(
                    { thursday: LocalDateTime -> thursday.plusHours(7) },
                    { thursday: LocalDateTime -> thursday.plusHours(8) }
                ),
                Arguments.of(
                    { thursday: LocalDateTime -> thursday.plusHours(8) },
                    { thursday: LocalDateTime -> thursday.plusDays(1).plusHours(8) }
                ),
                Arguments.of(
                    { thursday: LocalDateTime -> thursday.plusDays(1).plusHours(8) },
                    { thursday: LocalDateTime -> thursday.plusDays(4).plusHours(8) }
                )
            )
        }
    }
}