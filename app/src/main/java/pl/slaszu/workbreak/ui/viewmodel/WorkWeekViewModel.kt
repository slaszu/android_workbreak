package pl.slaszu.workbreak.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import pl.slaszu.workbreak.domain.model.WorkDay
import pl.slaszu.workbreak.domain.model.WorkHours
import pl.slaszu.workbreak.domain.model.WorkWeek
import pl.slaszu.workbreak.domain.model.WorkWeekRepository
import kotlin.random.Random

@HiltViewModel
class WorkWeekViewModel @Inject constructor(
    private val WorkWeekRepository: WorkWeekRepository
) : ViewModel() {
    // Expose screen UI state
    val workWeekFlow = this.WorkWeekRepository.get()

    fun save(workWeek: WorkWeek) {
        viewModelScope.launch {
            WorkWeekRepository.persist(
                workWeek.copy(
                    workDays = listOf(
                        WorkDay(
                            dayOfWeek = DayOfWeek.MONDAY,
                            workHours = WorkHours(8, 0, 16, 0),
                            breakEveryXMinutes = Random.nextInt(10, 55),
                            breakDurationMinutes = Random.nextInt(1, 5)
                        )
                    )
                )
            )
        }

    }
}