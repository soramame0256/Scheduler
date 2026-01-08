package com.github.soramame0256.scheduler.service

import com.github.soramame0256.scheduler.model.Time
import com.github.soramame0256.scheduler.model.TimeRange

interface TimeRangeWriter {
    suspend fun insertTimeRange(startTime: Time, endTime: Time): TimeRange
    suspend fun updateTimeRange(timeRange: TimeRange)
    suspend fun deleteTimeRange(timeRange: TimeRange)
}
