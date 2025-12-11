package com.github.soramame0256.scheduler.service

import com.github.soramame0256.scheduler.domain.Time
import com.github.soramame0256.scheduler.domain.TimeRange

interface TimeRangeWriter {
    suspend fun insertTimeRange(startTime: Time, endTime: Time): TimeRange
    suspend fun updateTimeRange(timeRange: TimeRange)
    suspend fun deleteTimeRange(timeRange: TimeRange)
}
