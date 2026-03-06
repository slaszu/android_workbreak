package pl.slaszu.workbreak.ui.viewmodel

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.toJavaLocalDateTime
import pl.slaszu.workbreak.application.CopyWorkDay
import pl.slaszu.workbreak.application.SetScheduleAlarm
import pl.slaszu.workbreak.application.SetWorkDay
import pl.slaszu.workbreak.domain.model.alarm.Alarm
import pl.slaszu.workbreak.domain.model.setting.Setting
import pl.slaszu.workbreak.domain.model.work.WorkDay
import pl.slaszu.workbreak.domain.model.work.WorkWeek
import pl.slaszu.workbreak.domain.presentation.AlarmPresentationFactory
import pl.slaszu.workbreak.domain.repository.SettingRepository
import pl.slaszu.workbreak.domain.repository.WorkWeekRepository
import java.time.LocalDateTime
import java.time.ZoneId

@HiltViewModel
class AppViewModel @Inject constructor(
    private val workWeekRepository: WorkWeekRepository,
    private val settingRepository: SettingRepository,
    private val useCaseSetWorkDay: SetWorkDay,
    private val useCaseSetScheduleAlarm: SetScheduleAlarm,
    private val useCaseCopyWorkDay: CopyWorkDay,
    private val alarmPresentationFactory: AlarmPresentationFactory
) : ViewModel() {

    private var lastAlarm: Alarm? = null

    private var snackbarHostState: SnackbarHostState? = null


    val setting = this.settingRepository.get()

    // Expose screen UI state
    val workWeekFlow = this.workWeekRepository.get()

    init {
        viewModelScope.launch {

            val setting = settingRepository.get().first()

            workWeekFlow.first {
                updateScheduleIfNeeded(
                    workWeek = it,
                    setting = setting
                )
                true
            }
        }
    }

    fun registerSnackbarHostState(snackbarHostState: SnackbarHostState) {
        this.snackbarHostState = snackbarHostState
    }

    fun setWorkDay(workWeek: WorkWeek, workDay: WorkDay) {
        val newWorkWeek = useCaseSetWorkDay.setWorkDay(workWeek, workDay)
        viewModelScope.launch {
            workWeekRepository.persist(
                newWorkWeek
            )

            val setting = settingRepository.get().first()
            updateScheduleIfNeeded(newWorkWeek, setting)
        }
    }

    fun setSetting(setting: Setting) {
        viewModelScope.launch {
            settingRepository.persist(setting)

            val workWeek = workWeekFlow.first()
            updateScheduleIfNeeded(workWeek, setting)
        }
    }

    fun copyWorkDay(workWeek: WorkWeek, copyDay: WorkDay, daysSelected: List<WorkDay>) {
        val newWorkWeek = useCaseCopyWorkDay.copyWorkDay(workWeek, copyDay, daysSelected)
        viewModelScope.launch {
            workWeekRepository.persist(
                newWorkWeek
            )
        }
    }

    suspend fun updateScheduleIfNeeded(workWeek: WorkWeek, setting: Setting) {
        val now = LocalDateTime.now(ZoneId.systemDefault())
        val alarm = useCaseSetScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeek,
            nowTime = now,
            startWorkAlarmFlag = setting.showWorkStartReminder,
            endWorkAlarmFlag = setting.showWorkEndReminder,
            muteUntil = setting.muteUntil?.toJavaLocalDateTime()
        )

        Log.d("myapp", "updateScheduleIfNeeded alarm: $alarm")

        snackbarHostState?.currentSnackbarData?.dismiss()

        if (lastAlarm != alarm) {
            snackbarHostState?.showSnackbar(
                message = getMessageForAlarm(alarm)
            )
        }

        lastAlarm = alarm
    }

    private fun getMessageForAlarm(alarm: Alarm?): String {
        if (alarm == null) {
            return "No active alarm"
        }

        val presentation = alarmPresentationFactory.create(alarm)
        return "${presentation.typeDescription} ${presentation.dateFormatted}"
    }
}