package pl.slaszu.workbreak

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import pl.slaszu.workbreak.domain.NotificationService
import pl.slaszu.workbreak.domain.ScheduleService
import pl.slaszu.workbreak.domain.model.WorkWeek
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

            val workWeek = viewModel.workWeekFlow.collectAsStateWithLifecycle(WorkWeek()).value

            WorkBreakTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeekConfiguration(
                        workWeek = workWeek,
                        onSave = {
                            viewModel.save(it)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun WeekConfiguration(
    workWeek: WorkWeek,
    onSave: (WorkWeek) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text("$workWeek")
        Button(
            onClick = {
                onSave(workWeek)
            }
        ) {
            Text(text = "save")
        }
    }
}