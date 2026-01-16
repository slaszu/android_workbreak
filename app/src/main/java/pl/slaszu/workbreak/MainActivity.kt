package pl.slaszu.workbreak

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.datetime.DayOfWeek
import pl.slaszu.workbreak.domain.Days
import pl.slaszu.workbreak.domain.model.WorkDay
import pl.slaszu.workbreak.domain.model.WorkWeek
import pl.slaszu.workbreak.domain.notification.NotificationService
import pl.slaszu.workbreak.domain.schedule.ScheduleService
import pl.slaszu.workbreak.ui.theme.WorkBreakTheme
import pl.slaszu.workbreak.ui.viewmodel.WorkWeekViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var notificationService: NotificationService

    @Inject
    lateinit var scheduleService: ScheduleService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!notificationService.hasPermission() || notificationService.shouldShowRationale(this)) {
            this.startActivity(
                Intent(this, NotificationRequestActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }

        if (!scheduleService.hasPermission()) {
            this.startActivity(
                Intent(this, ScheduleRequestActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }

        setContent {

            val viewModel by viewModels<WorkWeekViewModel>()

            val workWeek =
                viewModel.workWeekFlow.collectAsStateWithLifecycle(WorkWeek.create()).value

            WorkBreakTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text("$workWeek", modifier = Modifier.padding(innerPadding))
                    WeekConfiguration(
                        workWeek = workWeek,
                        onActivityChange = { workWeek, dayOfWeek, active ->
                            viewModel.setWorkDayActive(workWeek, dayOfWeek, active)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekConfiguration(
    workWeek: WorkWeek,
    modifier: Modifier = Modifier,
    onActivityChange: (WorkWeek, DayOfWeek, Boolean) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(items = Days.entries) {
            DayItem(
                day = it,
                workDay = workWeek.getWorkDay(it.dayOfWeek),
                onCheckedChange = { checked ->
                    onActivityChange(workWeek, it.dayOfWeek, checked)
                }
            )
        }
    }
}

@Composable
private fun DayItem(
    day: Days,
    workDay: WorkDay,
    onCheckedChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row {
        Checkbox(
            checked = workDay.active,
            onCheckedChange = onCheckedChange
        )
        Text(stringResource(day.dayTranslationKey))
    }
}

