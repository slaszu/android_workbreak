package pl.slaszu.workbreak.ui.screen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material.icons.rounded.StopCircle
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.WorkOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import pl.slaszu.workbreak.domain.Days
import pl.slaszu.workbreak.domain.model.work.WorkDay
import pl.slaszu.workbreak.domain.model.work.getBreakDurationMinutes
import pl.slaszu.workbreak.domain.model.work.getBreaksQuantity
import pl.slaszu.workbreak.domain.model.work.getWorkDurationMinutes
import pl.slaszu.workbreak.domain.utils.asMinutesToHoursAndMinutes
import pl.slaszu.workbreak.ui.theme.WorkBreakTheme

private data class TimePickerContainer(
    val initialHour: Int,
    val initialMinute: Int,
    val onConfirm: (Int, Int) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialogConfigurable(
    timePickerContainer: TimePickerContainer?,
    show: Boolean,
    onDismiss: () -> Unit
) {
    if (timePickerContainer == null) return

    val timePickerState = rememberTimePickerState(
        initialHour = timePickerContainer.initialHour,
        initialMinute = timePickerContainer.initialMinute,
        is24Hour = true,
    )


    if (show) {
        TimePickerDialog(
            onDismiss = onDismiss,
            onConfirm = {
                Log.d("myapp", "${timePickerState.hour}-${timePickerState.minute}")
                timePickerContainer.onConfirm(timePickerState.hour, timePickerState.minute)
                onDismiss()
            }
        ) {
            TimePicker(
                state = timePickerState,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayEditComposable(
    workDay: WorkDay,
    onSave: (WorkDay) -> Unit = {}
) {

    var timePickerShow by remember { mutableStateOf(false) }
    var timePickerContainer by remember { mutableStateOf<TimePickerContainer?>(null) }

    TimePickerDialogConfigurable(
        timePickerContainer = timePickerContainer,
        show = timePickerShow,
        onDismiss = {
            timePickerShow = false
            timePickerContainer = null
        }
    )

// Dodajemy przewijanie, aby formularz był bezpieczny na małych ekranach
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // --- NAGŁÓWEK DNIA ---
        val dayName = stringResource(Days.getForDayOfWeek(workDay.dayOfWeek).dayTranslationKey)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dayName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            // Przełącznik aktywności dnia przenieśliśmy na górę dla lepszego UX
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (workDay.active) "Active" else "Inactive",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (workDay.active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Switch(
                    checked = workDay.active,
                    onCheckedChange = { onSave(workDay.copy(active = it)) },
                    modifier = Modifier.scale(0.8f)
                )
            }
        }

// --- SEKCJA USTAWIEŃ (KARTA) ---
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                ParamInfo(
                    header = "Work start time",
                    desc = "When you start work",
                    icon = Icons.Rounded.PlayCircleOutline
                ) {
                    TextButton(
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = OutlinedTextFieldDefaults.shape,
                        contentPadding = OutlinedTextFieldDefaults.contentPadding(),
                        modifier = Modifier.weight(0.35f),
                        onClick = {
                            timePickerContainer = TimePickerContainer(
                                initialHour = workDay.workHours.startHour,
                                initialMinute = workDay.workHours.startMinute
                            ) { hour, minute ->
                                onSave(
                                    workDay.copy(
                                        workHours = workDay.workHours.copy(
                                            startHour = hour,
                                            startMinute = minute
                                        )
                                    )
                                )
                            }
                            timePickerShow = true
                        }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = workDay.workHours.startTime.toString()
                            )
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                ParamInfo(
                    header = "Work end time",
                    desc = "When you end work",
                    icon = Icons.Rounded.StopCircle,
                    error = if (workDay.workHours.timePeriod.endNextDay) {
                        "Next day. At ${workDay.workHours.endTime} on ${
                            stringResource(
                                Days.getNextDayOfWeek(
                                    workDay.dayOfWeek
                                ).dayTranslationKey
                            )
                        }"
                    } else null

                ) {
                    TextButton(
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = OutlinedTextFieldDefaults.shape,
                        contentPadding = OutlinedTextFieldDefaults.contentPadding(),
                        modifier = Modifier.weight(0.35f),
                        onClick = {
                            timePickerContainer = TimePickerContainer(
                                initialHour = workDay.workHours.endHour,
                                initialMinute = workDay.workHours.endMinute
                            ) { hour, minute ->
                                onSave(
                                    workDay.copy(
                                        workHours = workDay.workHours.copy(
                                            endHour = hour,
                                            endMinute = minute
                                        )
                                    )
                                )
                            }
                            timePickerShow = true
                        }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = workDay.workHours.endTime.toString()
                            )
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                ParamInfo(
                    header = "Work interval",
                    desc = "Minutes between breaks",
                    icon = Icons.Rounded.Timer
                ) {

                    val breakEveryXMinutes = rememberTextFieldState("${workDay.breakEveryXMinutes}")
                    LaunchedEffect(workDay) {
                        snapshotFlow { breakEveryXMinutes.text.toString() }.collectLatest {
                            if (it == workDay.breakEveryXMinutes.toString()) return@collectLatest
                            delay(150)
                            onSave(
                                workDay.copy(
                                    breakEveryXMinutes = it.toIntOrNull() ?: 0
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        state = breakEveryXMinutes,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        ),
                        lineLimits = TextFieldLineLimits.SingleLine,
                        inputTransformation = InputTransformation.maxLength(3)
                            .then {
                                if (!asCharSequence().isDigitsOnly()) {
                                    revertAllChanges()
                                }
                            },
                        textStyle = TextStyle(
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        ),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        },
                        modifier = Modifier.weight(0.35f)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                ParamInfo(
                    header = "Break length",
                    desc = "Duration of each break",
                    icon = Icons.Rounded.Coffee
                ) {

                    val breakDurationMinutes =
                        rememberTextFieldState("${workDay.breakDurationMinutes}")
                    LaunchedEffect(workDay) {
                        snapshotFlow { breakDurationMinutes.text.toString() }.collectLatest {
                            if (it == workDay.breakDurationMinutes.toString()) return@collectLatest
                            delay(150)
                            onSave(
                                workDay.copy(
                                    breakDurationMinutes = it.toIntOrNull() ?: 0
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        state = breakDurationMinutes,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        ),
                        lineLimits = TextFieldLineLimits.SingleLine,
                        inputTransformation = InputTransformation.maxLength(3)
                            .then {
                                if (!asCharSequence().isDigitsOnly()) {
                                    revertAllChanges()
                                }
                            },
                        textStyle = TextStyle(
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        ),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        },
                        modifier = Modifier.weight(0.35f)
                    )
                }
            }
        }

        WorkDaySummary(
            workDay = workDay
        )

    }


}

@Composable
private fun WorkDaySummary(workDay: WorkDay) {
    val totalWorkMin = workDay.getWorkDurationMinutes()
    val totalBreakMin = workDay.getBreakDurationMinutes()
    val totalDayMin = (totalWorkMin + totalBreakMin).coerceAtLeast(1) // Unikamy dzielenia przez 0

    // Obliczanie procentowego udziału dla paska wizualnego
    val workWeight = totalWorkMin.toFloat() / totalDayMin
    val breakWeight = totalBreakMin.toFloat() / totalDayMin

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- TYTUŁ I STATUS ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(isActive = workDay.active)
            }

            // --- VISUAL TIMELINE BAR ---
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    // Pasek pracy
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(workWeight.coerceAtLeast(0.01f))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    // Pasek przerw
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(breakWeight.coerceAtLeast(0.01f))
                            .background(MaterialTheme.colorScheme.tertiary)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LegendItem("Work", MaterialTheme.colorScheme.primary)
                    LegendItem("Breaks", MaterialTheme.colorScheme.tertiary)
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // --- STATS GRID ---
            Row(modifier = Modifier.fillMaxWidth()) {
                StatItem(
                    label = "Total Work",
                    value = totalWorkMin.asMinutesToHoursAndMinutes(),
                    icon = Icons.Rounded.WorkOutline,
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "Total Breaks",
                    value = totalBreakMin.asMinutesToHoursAndMinutes(),
                    icon = Icons.Rounded.Coffee,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                StatItem(
                    label = "Break Count",
                    value = "${workDay.getBreaksQuantity()}",
                    icon = Icons.Rounded.Tag,
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "Efficiency",
                    value = "${(workWeight * 100).toInt()}%",
                    icon = Icons.Rounded.Insights,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(isActive: Boolean) {
    val color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = CircleShape,
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = if (isActive) "Active" else "Inactive",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ParamInfo(
    header: String,
    desc: String,
    icon: ImageVector,
    error: String? = null,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 10.dp)
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(0.7f)
        ) {

            Text(
                text = header,
                fontSize = TextUnit(4f, TextUnitType.Em),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = desc,
                fontSize = TextUnit(3f, TextUnitType.Em),
            )

            error?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        content()
    }
}

@Composable
private fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Dismiss")
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("OK")
            }
        },
        text = { content() }
    )
}

@Preview
@Composable
private fun Preview() {
    WorkBreakTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(it)
            ) {
                DayEditComposable(
                    workDay = WorkDay.create(Days.MONDAY.dayOfWeek),
                    onSave = {

                    }
                )
            }
        }
    }
}