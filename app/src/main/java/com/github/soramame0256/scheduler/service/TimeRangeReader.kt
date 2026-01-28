package com.github.soramame0256.scheduler.service

import com.github.soramame0256.scheduler.model.Time
import com.github.soramame0256.scheduler.model.TimeRange

interface TimeRangeReader {
    suspend fun getAllTimeRanges(): List<TimeRange>
    suspend fun countConflicts(start: Time, end: Time): Int
}
