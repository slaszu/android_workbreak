package pl.slaszu.workbreak.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.datetime.DayOfWeek
import pl.slaszu.workbreak.domain.Days
import pl.slaszu.workbreak.domain.model.WorkDay
import pl.slaszu.workbreak.domain.model.WorkWeek

@Composable
fun ListOfDaysComposable(
    workWeek: WorkWeek,
    modifier: Modifier = Modifier,
    onActivityChange: (WorkWeek, DayOfWeek, Boolean) -> Unit,
    onDayClick: (DayOfWeek) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(items = Days.entries) {
            DayComposable(
                day = it,
                workDay = workWeek.getWorkDay(it.dayOfWeek),
                onCheckedChange = { checked ->
                    onActivityChange(workWeek, it.dayOfWeek, checked)
                },
                modifier = Modifier.clickable() {
                    onDayClick(it.dayOfWeek)
                }
            )
        }
    }
}

@Composable
private fun DayComposable(
    day: Days,
    workDay: WorkDay,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
    ) {
        Row {
            Checkbox(
                checked = workDay.active,
                onCheckedChange = onCheckedChange
            )
            Text(stringResource(day.dayTranslationKey))
        }

        Text("${workDay.workHours}")
        Text("${workDay.breakEveryXMinutes} & ${workDay.breakDurationMinutes}")

    }
}