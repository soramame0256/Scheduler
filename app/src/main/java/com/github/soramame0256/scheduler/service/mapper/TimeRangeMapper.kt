package com.github.soramame0256.scheduler.service.mapper

import com.github.soramame0256.scheduler.domain.TimeRange
import com.github.soramame0256.scheduler.repository.entity.TimeRangeEntity

object TimeRangeMapper {
    fun toDomain(entity: TimeRangeEntity): TimeRange =
        TimeRange(id = entity.timetableId, startTime = entity.start, endTime = entity.endTime)

    fun toEntity(domain: TimeRange): TimeRangeEntity =
        TimeRangeEntity(timetableId = domain.id, start = domain.startTime, endTime = domain.endTime)
}