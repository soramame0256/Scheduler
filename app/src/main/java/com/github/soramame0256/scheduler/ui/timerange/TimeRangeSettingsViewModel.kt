package com.github.soramame0256.scheduler.ui.timerange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soramame0256.scheduler.model.Time
import com.github.soramame0256.scheduler.model.TimeRange
import com.github.soramame0256.scheduler.service.ScheduleService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeRangeSettingsViewModel @Inject constructor(
    private val service: ScheduleService
) : ViewModel() {
    private val _timeRanges = MutableStateFlow<List<TimeRange>>(emptyList())
    val timeRanges: StateFlow<List<TimeRange>> = _timeRanges

    private val _events = MutableStateFlow<EventWrapper<UiEvent>>(EventWrapper(UiEvent.NoOperation()))
    val events: StateFlow<EventWrapper<UiEvent>> = _events

    private val _startTime = MutableStateFlow(Time(0, 0))
    val startTime: StateFlow<Time> = _startTime
    private val _endTime = MutableStateFlow(Time(0, 0))
    val endTime: StateFlow<Time> = _endTime

    fun load() = viewModelScope.launch {
        _timeRanges.value = service.getAllTimeRanges().sortedBy { it.startTime }
    }
    fun delete(timeRange: TimeRange) = viewModelScope.launch {
        service.deleteTimeRange(timeRange)
        // filterによる削除のため、startTimeによるソート順序は保持される。
        _timeRanges.update { currentList -> currentList.filterNot { it.id == timeRange.id } }
    }

    fun add(start: Time, end: Time) = viewModelScope.launch {
        if (start >= end) {
            _events.value = EventWrapper(UiEvent.Message(MessageId.StartAfterEnd))
            return@launch
        }
        val conflicts = service.countConflicts(start, end)
        if (conflicts == 0) {
            val newTimeRange = service.insertTimeRange(start, end)
            resetInputTimes()
            _timeRanges.update { currentList ->
                val newList = currentList.toMutableList()
                // 適切な位置に挿入
                val insertionPoint = newList.binarySearchBy(newTimeRange.startTime) { it.startTime }
                    .let { if (it < 0) -(it + 1) else it }
                newList.add(insertionPoint, newTimeRange)
                newList
            }
            _events.value = EventWrapper(UiEvent.Message(MessageId.InsertSuccess))
        } else {
            _events.value = EventWrapper(UiEvent.Message(MessageId.Conflict))
        }
    }
    fun updateStartTime(newTime: Time) {
        _startTime.value = newTime
    }

    fun updateEndTime(newTime: Time) {
        _endTime.value = newTime
    }

    fun resetInputTimes() {
        _startTime.value = Time(0, 0)
        _endTime.value = Time(0, 0)
    }

}
class EventWrapper<out T>(private val event: T) {
    var handled = false
        private set
    fun handle(block: (T) -> Unit) {
        if (!handled) {
            handled = true
            block(event)
        }
    }
}

sealed class UiEvent {
    data class Message(val id: MessageId) : UiEvent()
    class NoOperation : UiEvent()
}

enum class MessageId {
    InsertSuccess, Conflict, StartAfterEnd
}

