package com.github.soramame0256.scheduler.backend.schedule.db

import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.TimeRange

interface TimeRangeWriter {
    suspend fun insertTimeRange(timeRange: TimeRange): Long
    suspend fun updateTimeRange(timeRange: TimeRange)
    suspend fun deleteTimeRange(timeRange: TimeRange)
}