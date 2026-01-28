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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeRangeSettingsViewModel @Inject constructor(
    private val service: ScheduleService
) : ViewModel() {
    private val _timeRanges = MutableStateFlow<List<TimeRange>>(emptyList())
    val timeRanges: StateFlow<List<TimeRange>> = _timeRanges

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events

    fun load() = viewModelScope.launch {
        _timeRanges.value = service.getAllTimeRanges()
    }
    fun delete(timeRange: TimeRange) = viewModelScope.launch {
        service.deleteTimeRange(timeRange)
        _timeRanges.value = _timeRanges.value.filterNot { it.id == timeRange.id }
    }

    fun add(start: Time, end: Time) = viewModelScope.launch {
        if (start >= end) {
            _events.emit(UiEvent.Message(MessageId.StartAfterEnd))
            return@launch
        }
        val conflicts = service.countConflicts(start, end)
        if (conflicts == 0) {
            val newTimeRange = service.insertTimeRange(start, end)
            _timeRanges.value = (_timeRanges.value + newTimeRange).sortedBy { it.startTime }
            _events.emit(UiEvent.Message(MessageId.InsertSuccess))
        } else {
            _events.emit(UiEvent.Message(MessageId.Conflict))
        }
    }
}

sealed class UiEvent {
    data class Message(val id: MessageId) : UiEvent()
}

enum class MessageId {
    InsertSuccess, Conflict, StartAfterEnd
}