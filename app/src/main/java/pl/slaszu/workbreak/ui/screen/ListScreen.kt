package pl.slaszu.workbreak.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
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
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.padding(2.dp, 5.dp)
    ) {
        Checkbox(
            checked = workDay.active,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.weight(0.1f)
        )
        Column(
            modifier = Modifier.weight(0.8f)
        ) {
            Text(
                text = stringResource(day.dayTranslationKey),
                fontSize = TextUnit(4f, TextUnitType.Em)
            )
            Text("Work hours: ${workDay.workHours.startTime} - ${workDay.workHours.endTime}")
            Text("Break every ${workDay.breakEveryXMinutes} minutes (every break: ${workDay.breakDurationMinutes} minutes)")
        }
        IconButton(
            onClick = {},
            modifier = Modifier.weight(0.1f)
        ) {
            Icon(Icons.Filled.ContentCopy, null)
        }
    }
}