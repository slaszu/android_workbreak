package pl.slaszu.workbreak

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import pl.slaszu.workbreak.domain.model.WorkWeek
import pl.slaszu.workbreak.domain.notification.NotificationService
import pl.slaszu.workbreak.domain.schedule.ScheduleService
import pl.slaszu.workbreak.ui.DayEditRoute
import pl.slaszu.workbreak.ui.ListRouting
import pl.slaszu.workbreak.ui.screen.DayEditComposable
import pl.slaszu.workbreak.ui.screen.ListOfDaysComposable
import pl.slaszu.workbreak.ui.theme.WorkBreakTheme
import pl.slaszu.workbreak.ui.viewmodel.WorkWeekViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var notificationService: NotificationService

    @Inject
    lateinit var scheduleService: ScheduleService

    @OptIn(ExperimentalMaterial3Api::class)
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

            val navController = rememberNavController()

            WorkBreakTheme {
                Scaffold(
                    topBar = {
                        TopAppBarElement(
                            navController = navController
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Column(
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        NavHost(navController = navController, startDestination = ListRouting) {


                            composable<ListRouting> {
                                ListOfDaysComposable(
                                    workWeek = workWeek,
                                    onActivityChange = { workWeek, dayOfWeek, active ->
                                        viewModel.setWorkDayActive(workWeek, dayOfWeek, active)
                                    },
                                    onDayClick = { day ->
                                        navController.navigate(DayEditRoute(day))
                                    }
                                )
                            }


                            composable<DayEditRoute> { backStackEntry ->
                                val day: DayEditRoute = backStackEntry.toRoute()
                                DayEditComposable(
                                    workDay = workWeek.getWorkDay(day.day),
                                    onSave = { workDay ->
                                        viewModel.setWorkDay(workWeek, workDay)
                                    }
                                )
                            }


                        }
                    }


                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarElement(
    navController: NavHostController
) {
    val route by navController.currentBackStackEntryAsState()


    TopAppBar(
        actions = {
            IconButton(
                onClick = { navController.navigate(ListRouting) }
            ) {
                Icon(Icons.Filled.Settings, null)
            }
        },
        navigationIcon = {
            if (route?.destination?.hasRoute(ListRouting::class) != true) {
                IconButton(
                    onClick = { navController.navigate(ListRouting) }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            }
        },
        title = {
            Text("Work break reminder")
        }
    )
}
