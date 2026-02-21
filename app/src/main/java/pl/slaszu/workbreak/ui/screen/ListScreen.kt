package pl.slaszu.workbreak.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DayOfWeek
import pl.slaszu.workbreak.application.SetWorkDay
import pl.slaszu.workbreak.domain.Days
import pl.slaszu.workbreak.domain.model.work.WorkDay
import pl.slaszu.workbreak.domain.model.work.WorkWeek
import pl.slaszu.workbreak.ui.theme.WorkBreakTheme

@Composable
fun ListOfDaysComposable(
    workWeek: WorkWeek,
    modifier: Modifier = Modifier,
    onActivityChange: (WorkDay, Boolean) -> Unit,
    onDayClick: (DayOfWeek) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp), // Odstęp od krawędzi ekranu
        verticalArrangement = Arrangement.spacedBy(12.dp) // Odstęp między kartami
    ) {
        items(items = Days.entries) { dayEntry ->
            val workDay = workWeek.getWorkDay(dayEntry.dayOfWeek)
            DayCard(
                day = dayEntry,
                workDay = workDay,
                onCheckedChange = { checked ->
                    onActivityChange(workDay, checked)
                },
                onClick = { onDayClick(dayEntry.dayOfWeek) }
            )
        }
    }
}

@Composable
private fun DayCard(
    day: Days,
    workDay: WorkDay,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    // Wizualne wygaszenie, gdy dzień jest nieaktywny
    val alpha = if (workDay.active) 1f else 0.6f

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Checkbox po lewej
            Checkbox(
                checked = workDay.active,
                onCheckedChange = onCheckedChange
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 2. Informacje o dniu
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(day.dayTranslationKey),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (workDay.active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Godziny pracy
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${workDay.workHours.startTime} - ${workDay.workHours.endTime}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Informacja o przerwie
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        Icons.Rounded.Coffee,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Every ${workDay.breakEveryXMinutes}m (${workDay.breakDurationMinutes}m break)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 3. Przycisk kopiowania
            IconButton(
                onClick = { /* TODO: Logika kopiowania */ },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy settings")
            }
        }
    }
}

@Preview
@Composable
fun ListOfDayComposablePreview() {
    WorkBreakTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(it)
            ) {
                ListOfDaysComposable(
                    workWeek = WorkWeek.create().let {
                        val setDayActive = SetWorkDay()
                        setDayActive.setWorkDay(
                            it,
                            it.getWorkDay(DayOfWeek.THURSDAY).copy(active = true)
                        )
                    },
                    onActivityChange = { _, _ -> },
                    onDayClick = {}
                )
            }
        }
    }
}