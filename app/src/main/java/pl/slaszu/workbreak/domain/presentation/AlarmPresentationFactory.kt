package pl.slaszu.workbreak.domain.presentation


import jakarta.inject.Inject
import pl.slaszu.workbreak.R
import pl.slaszu.workbreak.domain.model.alarm.Alarm
import pl.slaszu.workbreak.domain.utils.getDateTimeFormatted
import pl.slaszu.workbreak.domain.utils.getDayName

class AlarmPresentationFactory @Inject constructor() {

    fun create(alarm: Alarm): AlarmPresentation {
        /*
        val polskiFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy, HH:mm", Locale("pl", "PL"))
        val angielskiFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a", Locale.US)
         */

        val dateFormatted = alarm.alarmDateTime.getDateTimeFormatted()
        val dayName = alarm.alarmDateTime.getDayName()
        val dayOfYear = alarm.alarmDateTime.dayOfYear

        val index = (dayOfYear + alarm.alarmDateTime.hour) % 7

        return when (alarm) {
            is Alarm.BreakStart -> {
                val descriptions = listOf(
                    "Recharging is part of the process. Rest well.",
                    "Step away for a moment. Your brain will thank you.",
                    "Time to stretch and hydrate. Be right back!",
                    "Pause the engine. Fresh air is calling.",
                    "Break time! Short rest, better focus later.",
                    "Coffee or tea? Now is the perfect time.",
                    "Disconnect to reconnect. Enjoy your breather."
                )
                AlarmPresentation(
                    icon = R.drawable.baseline_timer_24,
                    header = "Time for a break",
                    description = descriptions[index],
                    dateFormatted = dateFormatted,
                    typeDescription = "Break start"
                )
            }

            is Alarm.BreakEnd -> {
                val descriptions = listOf(
                    "Ready to focus? Let's get back to the session.",
                    "Break over. Let's maintain that momentum!",
                    "Welcome back. Time to crush the next task.",
                    "Systems back online. Let's resume work.",
                    "Refreshed and ready? The charts are waiting.",
                    "Focus mode: ON. Let's continue the progress.",
                    "Back to the grind. Stay sharp!"
                )
                AlarmPresentation(
                    icon = R.drawable.baseline_timer_off_24,
                    header = "Break finished",
                    description = descriptions[index],
                    dateFormatted = dateFormatted,
                    typeDescription = "Break end"
                )
            }

            is Alarm.WorkStart -> {
                val descriptions = listOf(
                    "Today is $dayName (Day $dayOfYear). Let's make it productive!",
                    "New day, new opportunities. Strategy set, let's go.",
                    "It's $dayName. A perfect day for consistent progress.",
                    "Session $dayOfYear started. Stay disciplined and focused.",
                    "Good morning! Ready to execute the plan for $dayName?",
                    "Day $dayOfYear: All systems nominal. Let's work.",
                    "The market of life is open. Make the most of this $dayName!"
                )
                AlarmPresentation(
                    icon = R.drawable.baseline_timer_24,
                    header = "Shift started",
                    description = descriptions[index],
                    dateFormatted = dateFormatted,
                    typeDescription = "Work start"
                )
            }

            is Alarm.WorkEnd -> {
                val descriptions = listOf(
                    "Great job today. All systems closed. Enjoy your free time!",
                    "Shift complete. Logged out and ready to relax.",
                    "The work is done. Time to recharge.",
                    "System shutdown successful. See you soon!",
                    "Focus session ended. Transitioning to rest mode.",
                    "Another day, another win. Time to unplug.",
                    "You've put in the work. Now enjoy your free time!"
                )
                AlarmPresentation(
                    icon = R.drawable.baseline_timer_off_24,
                    header = "Shift completed",
                    description = descriptions[index],
                    dateFormatted = dateFormatted,
                    typeDescription = "Work end"
                )
            }
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
