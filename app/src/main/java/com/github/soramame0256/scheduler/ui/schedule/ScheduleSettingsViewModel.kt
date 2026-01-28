package com.github.soramame0256.scheduler.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soramame0256.scheduler.model.Schedule
import com.github.soramame0256.scheduler.model.TimeRange
import com.github.soramame0256.scheduler.model.Weekday
import com.github.soramame0256.scheduler.service.ScheduleService
import com.github.soramame0256.scheduler.ui.EventWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleSettingsViewModel @Inject constructor(
    private val service: ScheduleService
): ViewModel() {

    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules
    private val _events = MutableStateFlow<EventWrapper<ScheduleUiEvent>>(EventWrapper(ScheduleUiEvent.NoOperation()))
    val events: StateFlow<EventWrapper<ScheduleUiEvent>> = _events


    fun load() {
    }
    fun delete(schedule: Schedule) {

    }
    fun add(weekday: Weekday, timeRange: TimeRange, message: String) = viewModelScope.launch {
        if (service.countConflictSchedules(weekday, timeRange) > 0) {
            _events.value = EventWrapper(ScheduleUiEvent.Message(ScheduleInsertMessageId.Conflict))
            return@launch
        }
        val newSchedule = Schedule(weekday, timeRange, message)
        resetInputs()
        service.insertSchedule(newSchedule)
        _schedules.update { currentList ->
            val newList = currentList.toMutableList()
            val insertionPoint = newList.binarySearchBy(newSchedule.weekday) { it.weekday }
                .let { if (it < 0) -(it + 1) else it }
            newList.add(insertionPoint, newSchedule)
            newList
        }
        _events.value = EventWrapper(ScheduleUiEvent.Message(ScheduleInsertMessageId.InsertSuccess))
    }

    private fun resetInputs() {
    }
}
sealed class ScheduleUiEvent {
    data class Message(val id: ScheduleInsertMessageId) : ScheduleUiEvent()
    class NoOperation : ScheduleUiEvent()
}

enum class ScheduleInsertMessageId {
    InsertSuccess, Conflict
}

