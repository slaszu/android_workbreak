package pl.slaszu.workbreak.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import pl.slaszu.workbreak.application.SetWorkDay
import pl.slaszu.workbreak.application.SetWorkDayActivity
import pl.slaszu.workbreak.domain.model.WorkDay
import pl.slaszu.workbreak.domain.model.WorkWeek
import pl.slaszu.workbreak.domain.model.WorkWeekRepository

@HiltViewModel
class WorkWeekViewModel @Inject constructor(
    private val WorkWeekRepository: WorkWeekRepository
) : ViewModel() {
    // Expose screen UI state
    val workWeekFlow = this.WorkWeekRepository.get()

    fun setWorkDayActive(workWeek: WorkWeek, dayOfWeek: DayOfWeek, active: Boolean) {
        val newWorkWeek = SetWorkDayActivity().setWorkDay(workWeek, dayOfWeek, active)
        viewModelScope.launch {
            WorkWeekRepository.persist(
                newWorkWeek
            )
        }
    }

    fun setWorkDay(workWeek: WorkWeek, workDay: WorkDay) {
        val newWorkWeek = SetWorkDay().setWorkDay(workWeek, workDay)
        viewModelScope.launch {
            WorkWeekRepository.persist(
                newWorkWeek
            )
            Log.d("myapp", "setWorkDay: $newWorkWeek")
        }
    }
}