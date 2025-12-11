package com.github.soramame0256.scheduler.service

import com.github.soramame0256.scheduler.repository.entity.TimeRangeEntity

interface TimeRangeReader {
    suspend fun getAllTimeRanges(): List<TimeRangeEntity>
}
