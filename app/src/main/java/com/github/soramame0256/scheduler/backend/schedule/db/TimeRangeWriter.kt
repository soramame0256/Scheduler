package com.github.soramame0256.scheduler.backend.schedule.db

import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.TimeRange
import java.util.concurrent.CompletableFuture

interface TimeRangeWriter {
    suspend fun insertTimeRange(timeRange: TimeRange)
    suspend fun updateTimeRange(timeRange: TimeRange)
    suspend fun deleteTimeRange(timeRange: TimeRange)
}