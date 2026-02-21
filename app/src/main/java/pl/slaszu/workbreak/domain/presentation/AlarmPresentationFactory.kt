package pl.slaszu.workbreak.domain.presentation


import jakarta.inject.Inject
import kotlinx.datetime.toJavaLocalDateTime
import pl.slaszu.workbreak.R
import pl.slaszu.workbreak.domain.model.alarm.Alarm
import java.time.format.DateTimeFormatter
import java.util.Locale

class AlarmPresentationFactory @Inject constructor() {

    fun create(alarm: Alarm): AlarmPresentation {
        /*
        val polskiFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy, HH:mm", Locale("pl", "PL"))
        val angielskiFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a", Locale.US)
         */

        val dateFormatted = alarm.alarmDateTime.toJavaLocalDateTime().format(
            DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a", Locale.US)
        )
        return when (alarm) {
            is Alarm.BreakStart -> AlarmPresentation(
                icon = R.drawable.baseline_timer_24,
                header = "Break start",
                description = "Enjoy the coffee",
                dateFormatted = dateFormatted,
                typeDescription = "Break start"
            )

            is Alarm.BreakEnd -> AlarmPresentation(
                icon = R.drawable.baseline_timer_off_24,
                header = "Break end",
                description = "Back to work",
                dateFormatted = dateFormatted,
                typeDescription = "Break end"
            )

            is Alarm.WorkStart -> AlarmPresentation(
                icon = R.drawable.baseline_timer_24,
                header = "Work start",
                description = "It is thursday, day of \"Pizza and Tomato\". 235 day of year",
                dateFormatted = dateFormatted,
                typeDescription = "Work start"
            )

            is Alarm.WorkEnd -> AlarmPresentation(
                icon = R.drawable.baseline_timer_off_24,
                header = "Work end",
                description = "See you soon, bye",
                dateFormatted = dateFormatted,
                typeDescription = "Work end"
            )
        }
    }
}

data class AlarmPresentation(
    val icon: Int,
    val header: String,
    val description: String,
    val dateFormatted: String,
    val typeDescription: String
)
