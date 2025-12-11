package com.github.soramame0256.scheduler.backend.schedule.service

import com.github.soramame0256.scheduler.backend.schedule.repository.entity.TimeRange

interface TimeRangeReader {
    suspend fun getAllTimeRanges(): List<TimeRange>
}
