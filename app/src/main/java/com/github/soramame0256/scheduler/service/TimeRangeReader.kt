package com.github.soramame0256.scheduler.service

import com.github.soramame0256.scheduler.domain.TimeRange

interface TimeRangeReader {
    suspend fun getAllTimeRanges(): List<TimeRange>
}
