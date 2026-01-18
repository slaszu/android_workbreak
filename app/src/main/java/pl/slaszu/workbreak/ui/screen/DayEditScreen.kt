package pl.slaszu.workbreak.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import pl.slaszu.workbreak.domain.Days
import pl.slaszu.workbreak.domain.model.WorkDay
import pl.slaszu.workbreak.ui.theme.WorkBreakTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayEditComposable(
    workDay: WorkDay,
    onSave: (WorkDay) -> Unit = {}
) {

    val timePickerState = rememberTimePickerState(
        initialHour = workDay.workHours.startHour,
        initialMinute = workDay.workHours.startMinute,
        is24Hour = true,
    )

    var chooseTimeDialog by remember { mutableStateOf(false) }
    if (chooseTimeDialog) {
        TimePickerDialog(
            onDismiss = {
                chooseTimeDialog = false
            },
            onConfirm = {
                Log.d("myapp", "${timePickerState.minute}-${timePickerState.hour}")
                chooseTimeDialog = false
            }
        ) {
            TimePicker(
                state = timePickerState,
            )
        }
    }

    Column(modifier = Modifier.padding(10.dp)) {

        Text(stringResource(Days.getForDayOfWeek(workDay.dayOfWeek).dayTranslationKey))

        ParamInfo(
            header = "Work hours",
            desc = "Description form work hours"
        ) {
            Text("${workDay.workHours.startHour} - ${workDay.workHours.endHour}")
            Button(
                onClick = {
                    chooseTimeDialog = true
                }
            ) {
                Text("Edit")
            }
        }

        ParamInfo(
            header = "Break every",
            desc = "Description form break every"
        ) {
            Text("${workDay.breakEveryXMinutes}")
        }

        ParamInfo(
            header = "Break duration",
            desc = "Description form break duration"
        ) {
            Text("${workDay.breakDurationMinutes}")
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