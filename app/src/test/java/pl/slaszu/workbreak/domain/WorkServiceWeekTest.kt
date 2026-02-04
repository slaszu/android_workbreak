package pl.slaszu.workbreak.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import pl.slaszu.workbreak.domain.model.WorkWeek
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

        val startDatetime =
            workService.getPrevDayOfWeek(LocalDateTime.now(), JavaDayOfWeek.THURSDAY)

        val workPeriodList = workService.toWorkPeriodList(
            workWeek = workWeek,
            dateTime = startDatetime
        )

        println(workPeriodList)

        val lookingForDateTime = datetimeModifier(startDatetime)

        println(lookingForDateTime)

        val workPeriod = workPeriodList.findWorkPeriod(lookingForDateTime)
        assertEquals(expectedType, workPeriod?.type)
    }

    companion object {
        @JvmStatic
        fun provide(): Stream<Arguments> {
            return Stream.of(


            )
        }
    }
}

