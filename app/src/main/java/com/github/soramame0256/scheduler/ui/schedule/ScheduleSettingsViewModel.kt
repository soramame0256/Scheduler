package com.github.soramame0256.scheduler.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soramame0256.scheduler.model.Schedule
import com.github.soramame0256.scheduler.model.Time
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

    private val _timeRanges = MutableStateFlow<List<TimeRange>>(emptyList())
    val timeRanges: StateFlow<List<TimeRange>> = _timeRanges
    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules
    private val _events = MutableStateFlow<EventWrapper<ScheduleUiEvent>>(EventWrapper(ScheduleUiEvent.NoOperation()))
    val events: StateFlow<EventWrapper<ScheduleUiEvent>> = _events

    private val _selectedWeekday = MutableStateFlow(Weekday.MONDAY)
    val selectedWeekday: StateFlow<Weekday> = _selectedWeekday

    private val _selectedTimeRange = MutableStateFlow(TimeRange(-1, Time(0,0), Time(0, 0)))
    val selectedTimeRange: StateFlow<TimeRange> = _selectedTimeRange

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private val _dontReset = MutableStateFlow(false)
    val dontReset: StateFlow<Boolean> = _dontReset

    fun load() = viewModelScope.launch {
        _timeRanges.value = service.getAllTimeRanges().sortedBy { it.startTime }
        _selectedTimeRange.value = _timeRanges.value.firstOrNull() ?: TimeRange(-1, Time(0,0), Time(0, 0))
        _schedules.value = service.getSchedules().sortedWith(compareBy({ it.weekday }, { it.timeRange.startTime }))    }
    fun delete(schedule: Schedule) = viewModelScope.launch {
        service.deleteSchedule(schedule)
        _schedules.value = _schedules.value.filterNot { schedule.weekday == it.weekday && schedule.timeRange.id == it.timeRange.id }
    }
    fun add(weekday: Weekday, timeRange: TimeRange, message: String) = viewModelScope.launch {
        if (service.countConflictSchedules(weekday, timeRange) > 0) {
            _events.value = EventWrapper(ScheduleUiEvent.Message(ScheduleInsertMessageId.Conflict))
            return@launch
        }
        if (timeRange.id == (-1).toLong()) {
            _events.value = EventWrapper(ScheduleUiEvent.Message(ScheduleInsertMessageId.InvalidTimeRange))
            return@launch
        }
        val newSchedule = Schedule(weekday, timeRange, message)
        resetInputs()
        service.insertSchedule(newSchedule)
        _schedules.update { currentList ->
            (currentList + newSchedule).sortedWith(compareBy({ it.weekday }, { it.timeRange.startTime }))
        }
        _events.value = EventWrapper(ScheduleUiEvent.Message(ScheduleInsertMessageId.InsertSuccess))
    }

    private fun resetInputs() {
        if (_dontReset.value) return
        _message.value = ""
    }
    fun updateSelectedWeekday(weekday: Weekday) {
        _selectedWeekday.value = weekday
    }
    fun updateSelectedTimeRange(timeRange: TimeRange) {
        _selectedTimeRange.value = timeRange
    }
    fun updateMessage(message: String) {
        _message.value = message
    }
    fun updateDontReset(dontReset: Boolean) {
        _dontReset.value = dontReset
    }
}
sealed class ScheduleUiEvent {
    data class Message(val id: ScheduleInsertMessageId) : ScheduleUiEvent()
    class NoOperation : ScheduleUiEvent()
}

enum class ScheduleInsertMessageId {
    InsertSuccess, Conflict, InvalidTimeRange
}

