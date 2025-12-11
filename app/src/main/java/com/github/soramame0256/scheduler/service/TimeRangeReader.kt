package com.github.soramame0256.scheduler.service

import com.github.soramame0256.scheduler.repository.entity.TimeRange

interface TimeRangeReader {
    suspend fun getAllTimeRanges(): List<TimeRange>
}
