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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
    onDayClick: (DayOfWeek) -> Unit,
    onCopyDaysAction: (WorkDay, List<WorkDay>) -> Unit
) {
    var rememberCopyDay by remember { mutableStateOf<WorkDay?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp), // Odstęp od krawędzi ekranu
        verticalArrangement = Arrangement.spacedBy(12.dp) // Odstęp między kartami
    ) {
        items(items = Days.entries) { dayEntry ->
            val workDay = workWeek.getWorkDay(dayEntry.dayOfWeek)
            DayCard(
                workDay = workDay,
                onCheckedChange = { checked ->
                    onActivityChange(workDay, checked)
                },
                onClickCopy = {
                    rememberCopyDay = it
                },
                onClick = { onDayClick(dayEntry.dayOfWeek) }
            )
        }
    }

    if (rememberCopyDay != null) {
        CopyDialog(
            copyDay = rememberCopyDay as WorkDay,
            workWeek = workWeek,
            onDismissRequest = { rememberCopyDay = null },
            onConfirm = { onCopyDaysAction(rememberCopyDay as WorkDay, it) }
        )
    }
}

@Composable
private fun DayCard(
    workDay: WorkDay,
    onCheckedChange: (Boolean) -> Unit,
    onClickCopy: (WorkDay) -> Unit,
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

            WorkDayInformation(
                workDay = workDay,
                modifier = Modifier.weight(1f)
            )

            // 3. Przycisk kopiowania
            IconButton(
                onClick = { onClickCopy(workDay) },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy settings")
            }
        }
    }
}

@Composable
private fun WorkDayInformation(
    workDay: WorkDay,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(Days.getForDayOfWeek(workDay.dayOfWeek).dayTranslationKey),
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
}

@Composable
private fun CopyDialog(
    copyDay: WorkDay,
    workWeek: WorkWeek,
    onDismissRequest: () -> Unit,
    onConfirm: (List<WorkDay>) -> Unit // Callback przekazujący wybrane dni
) {
    // Stan przechowujący wybrane dni (bez dnia źródłowego)
    var checkedDays by remember { mutableStateOf<List<WorkDay>>(emptyList()) }

    Dialog(onDismissRequest = onDismissRequest) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(28.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp) // Standardowy padding M3 dla dialogów
            ) {
                // Nagłówek
                Text(
                    text = "Copy settings to...",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Lista dni - ograniczona wysokość, by przyciski były zawsze widoczne
                LazyColumn(
                    modifier = Modifier.weight(weight = 1f, fill = false)
                ) {
                    items(items = Days.entries) { dayEntry ->
                        val isSourceDay = dayEntry.dayOfWeek == copyDay.dayOfWeek
                        val workDay =
                            if (isSourceDay) copyDay else workWeek.getWorkDay(dayEntry.dayOfWeek)

                        CopyDialogRow(
                            workDay = workDay,
                            checked = if (isSourceDay) true else checkedDays.contains(workDay),
                            disabled = isSourceDay,
                            onChecked = { isChecked ->
                                checkedDays = if (isChecked) {
                                    checkedDays + workDay
                                } else {
                                    checkedDays - workDay
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sekcja przycisków akcji
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onConfirm(checkedDays)
                            onDismissRequest()
                        },
                        enabled = checkedDays.isNotEmpty(), // Aktywny tylko gdy coś wybrano
                        shape = RoundedCornerShape(100) // Pigułkowy kształt przycisku M3
                    ) {
                        Text("Copy")
                    }
                }
            }
        }
    }
}

@Composable
private fun CopyDialogRow(
    workDay: WorkDay,
    checked: Boolean,
    disabled: Boolean = false,
    onChecked: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)) // Ripple (efekt kliknięcia) pasujący do karty
            .clickable(enabled = !disabled) { onChecked(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null, // Obsługa przez clickable w Row dla lepszego UX
            enabled = !disabled
        )

        Spacer(modifier = Modifier.width(12.dp))

        val alpha = if (workDay.active) 1f else 0.5f

        WorkDayInformation(
            workDay = workDay,
            modifier = Modifier
                .weight(1f)
                .alpha(alpha)
        )
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
                    workWeek = WorkWeek.createWeekInactive().let {
                        val setDayActive = SetWorkDay()
                        setDayActive.setWorkDay(
                            it,
                            it.getWorkDay(DayOfWeek.THURSDAY).copy(active = true)
                        )
                    },
                    onActivityChange = { _, _ -> },
                    onDayClick = {},
                    onCopyDaysAction = { _, _ -> }
                )
            }
        }
    }
}