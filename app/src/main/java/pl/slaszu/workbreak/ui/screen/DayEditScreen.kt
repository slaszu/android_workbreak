package pl.slaszu.workbreak.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
import pl.slaszu.workbreak.domain.model.WorkDay
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

    Column(modifier = Modifier.padding(10.dp)) {

        Text(stringResource(Days.getForDayOfWeek(workDay.dayOfWeek).dayTranslationKey))

        ParamInfo(
            header = "Start hour",
            desc = "Description form work hours"
        ) {
            Text("${workDay.workHours.startHour}:${workDay.workHours.startMinute}")
            Button(
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
                Text("Edit")
            }
        }

        ParamInfo(
            header = "End hour",
            desc = "Description form work hours"
        ) {
            Text("${workDay.workHours.endHour}:${workDay.workHours.endMinute}")
            Button(
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
                Text("Edit")
            }
        }

        ParamInfo(
            header = "Break every",
            desc = "Description form break every"
        ) {

            val breakEveryXMinutes = rememberTextFieldState("${workDay.breakEveryXMinutes}")
            LaunchedEffect(breakEveryXMinutes) {
                snapshotFlow { breakEveryXMinutes.text.toString() }.collectLatest {
                    if (it == workDay.breakEveryXMinutes.toString()) return@collectLatest
                    delay(300)
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
                    textAlign = TextAlign.End
                ),
                modifier = Modifier.weight(0.2f)
            )
        }

        ParamInfo(
            header = "Break duration",
            desc = "Description form break duration"
        ) {

            val breakDurationMinutes = rememberTextFieldState("${workDay.breakDurationMinutes}")
            LaunchedEffect(breakDurationMinutes) {
                snapshotFlow { breakDurationMinutes.text.toString() }.collectLatest {
                    if (it == workDay.breakDurationMinutes.toString()) return@collectLatest
                    delay(300)
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
                    textAlign = TextAlign.End
                ),
                modifier = Modifier.weight(0.2f)
            )
        }
    }
}

@Composable
private fun ParamInfo(
    header: String,
    desc: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 10.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(0.9f)
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
        }
        content()
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun TimeDialogModel(
//    onConfirm: (TimePickerState) -> Unit,
//    onDismiss: () -> Unit,
//) {
//    val currentTime = Calendar.getInstance()
//
//    val timePickerState = rememberTimePickerState(
//        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
//        initialMinute = currentTime.get(Calendar.MINUTE),
//        is24Hour = true,
//    )
//
//    TimePickerDialog(
//        onDismiss = { onDismiss() },
//        onConfirm = { onConfirm(timePickerState) }
//    ) {
//        TimePicker(
//            state = timePickerState,
//        )
//    }
//}

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