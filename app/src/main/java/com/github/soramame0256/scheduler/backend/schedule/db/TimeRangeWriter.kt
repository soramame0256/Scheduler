package com.github.soramame0256.scheduler.backend.schedule.db

import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.TimeRange
import java.util.concurrent.CompletableFuture

interface TimeRangeWriter {
    fun insertTimeRange(timeRange: TimeRange): CompletableFuture<Void>
    fun updateTimeRange(timeRange: TimeRange): CompletableFuture<Void>
    fun deleteTimeRange(timeRange: TimeRange): CompletableFuture<Void>
}