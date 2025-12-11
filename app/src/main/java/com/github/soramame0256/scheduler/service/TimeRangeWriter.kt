package com.github.soramame0256.scheduler.service

import com.github.soramame0256.scheduler.repository.entity.TimeRange

interface TimeRangeWriter {
    suspend fun insertTimeRange(timeRange: TimeRange): Long
    suspend fun updateTimeRange(timeRange: TimeRange)
    suspend fun deleteTimeRange(timeRange: TimeRange)
}
