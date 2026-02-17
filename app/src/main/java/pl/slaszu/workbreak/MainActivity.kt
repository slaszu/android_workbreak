package pl.slaszu.workbreak

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import pl.slaszu.workbreak.domain.model.Setting
import pl.slaszu.workbreak.domain.model.SettingRepository
import pl.slaszu.workbreak.domain.model.WorkWeek
import pl.slaszu.workbreak.domain.notification.NotificationPermissionService
import pl.slaszu.workbreak.domain.schedule.SchedulePermissionService
import pl.slaszu.workbreak.ui.DayEditRoute
import pl.slaszu.workbreak.ui.ListRouting
import pl.slaszu.workbreak.ui.SettingRoute
import pl.slaszu.workbreak.ui.element.TopBarElement
import pl.slaszu.workbreak.ui.screen.DayEditComposable
import pl.slaszu.workbreak.ui.screen.ListOfDaysComposable
import pl.slaszu.workbreak.ui.screen.SettingScreen
import pl.slaszu.workbreak.ui.theme.WorkBreakTheme
import pl.slaszu.workbreak.ui.viewmodel.AppViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var notificationPermissionService: NotificationPermissionService

    @Inject
    lateinit var schedulePermissionService: SchedulePermissionService

    @Inject
    lateinit var settingRepository: SettingRepository

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        startPermissionActivityIfNeeded()

        setContent {

            val viewModel by viewModels<AppViewModel>()

            val snackbarHostState = remember { SnackbarHostState() }
            viewModel.registerSnackbarHostState(snackbarHostState)

            val workWeek =
                viewModel.workWeekFlow.collectAsStateWithLifecycle(WorkWeek.create()).value

            val setting = viewModel.setting.collectAsStateWithLifecycle(Setting()).value

            val navController = rememberNavController()

            // 1. Definiujemy zachowanie (pinned oznacza, że bar nie znika, ale zmienia kolor)
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

            WorkBreakTheme {
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                        )
                    },
                    topBar = {
                        TopBarElement(
                            navController = navController,
                            scrollBehavior = scrollBehavior,
                            showBadge = !notificationPermissionService.hasPermission() || !schedulePermissionService.hasPermission()
                        )
                    },
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), // 2. Łączymy z przewijaniem
                ) { innerPadding ->
                    Column(
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = ListRouting::class
                        ) {


                            composable<ListRouting> {
                                ListOfDaysComposable(
                                    workWeek = workWeek,
                                    onActivityChange = { workDay, active ->
                                        viewModel.setWorkDay(
                                            workWeek = workWeek,
                                            workDay = workDay.copy(active = active),
                                            setting = setting
                                        )
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
                                        viewModel.setWorkDay(
                                            workWeek = workWeek,
                                            workDay = workDay,
                                            setting = setting
                                        )
                                    }
                                )
                            }


                            composable<SettingRoute> {
                                SettingScreen(
                                    setting = setting,
                                    onSave = {
                                        viewModel.setSetting(it)
                                    },
                                    notificationPermission = notificationPermissionService.hasPermission(),
                                    schedulePermission = schedulePermissionService.hasPermission(),
                                    onOpenSettingsForNotification = {
                                        startActivityForNotificationPermission()
                                    },
                                    onOpenSettingForSchedule = {
                                        startActivityForSchedulePermission()
                                    }
                                )
                            }

                        }
                    }
                }
            }
        }
    }

    private fun startPermissionActivityIfNeeded() {

        val schedule = schedulePermissionService.hasPermission()
        var notification = notificationPermissionService.hasPermission()
        if (!notification) {
            notification = notificationPermissionService.shouldShowRationale(this)
        }

        var setting = Setting()
        if (!schedule || !notification) {
            setting = runBlocking {
                settingRepository.get().first()
            }
        }

        if (!schedule && !setting.scheduleAlarmRequestDisplayed) {
            runBlocking {
                settingRepository.persist(
                    setting.copy(scheduleAlarmRequestDisplayed = true)
                )
            }
            startActivityForSchedulePermission()
            finish()
        }

        if (!notification && !setting.notificationRequestDisplayed) {
            runBlocking {
                settingRepository.persist(
                    setting.copy(notificationRequestDisplayed = true)
                )
            }
            startActivityForNotificationPermission()
            finish()
        }
    }

    private fun startActivityForSchedulePermission() {
        this.startActivity(
            Intent(this, ScheduleRequestActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }

    private fun startActivityForNotificationPermission() {
        this.startActivity(
            Intent(this, NotificationRequestActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }
}


