package pl.slaszu.workbreak.ui.viewmodel

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pl.slaszu.workbreak.application.SetScheduleAlarm
import pl.slaszu.workbreak.application.SetWorkDay
import pl.slaszu.workbreak.domain.model.WorkDay
import pl.slaszu.workbreak.domain.model.WorkWeek
import pl.slaszu.workbreak.domain.model.WorkWeekRepository
import java.time.LocalDateTime
import java.time.ZoneId

@HiltViewModel
class WorkWeekViewModel @Inject constructor(
    private val WorkWeekRepository: WorkWeekRepository,
    private val useCaseSetWorkDay: SetWorkDay,
    private val useCaseSetScheduleAlarm: SetScheduleAlarm
) : ViewModel() {

    private var lastAlarmDateTime: LocalDateTime? = null

    private var snackbarHostState: SnackbarHostState? = null

    // Expose screen UI state
    val workWeekFlow = this.WorkWeekRepository.get()

    init {
        viewModelScope.launch {
            workWeekFlow.first {
                updateScheduleIfNeeded(it)
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
            WorkWeekRepository.persist(
                newWorkWeek
            )
            Log.d("myapp", "setWorkDay: $newWorkWeek")
            updateScheduleIfNeeded(newWorkWeek)
        }
    }

    suspend fun updateScheduleIfNeeded(workWeek: WorkWeek) {
        val now = LocalDateTime.now(ZoneId.systemDefault())
        val alarmDateTime = useCaseSetScheduleAlarm.setNextBreakScheduleAlarm(workWeek, now)

        Log.d("myapp", "updateScheduleIfNeeded alarmDateTime: $alarmDateTime")

        snackbarHostState?.currentSnackbarData?.dismiss()

        if (lastAlarmDateTime != alarmDateTime) {
            snackbarHostState?.showSnackbar("Alarm updated: $alarmDateTime")
        }

        lastAlarmDateTime = alarmDateTime
    }
}