package com.github.soramame0256.scheduler.service

import com.github.soramame0256.scheduler.repository.entity.TimeRangeEntity

interface TimeRangeWriter {
    suspend fun insertTimeRange(timeRange: TimeRangeEntity): Long
    suspend fun updateTimeRange(timeRange: TimeRangeEntity)
    suspend fun deleteTimeRange(timeRange: TimeRangeEntity)
}
