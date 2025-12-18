package com.github.soramame0256.scheduler.repository.mapper

import com.github.soramame0256.scheduler.model.TimeRange
import com.github.soramame0256.scheduler.repository.entity.TimeRangeEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeRangeMapperImpl @Inject constructor() : TimeRangeMapper {
    override fun toDomain(entity: TimeRangeEntity): TimeRange =
        TimeRange(id = entity.timetableId, startTime = entity.start, endTime = entity.endTime)

    override fun toEntity(domain: TimeRange): TimeRangeEntity =
        TimeRangeEntity(timetableId = domain.id, start = domain.startTime, endTime = domain.endTime)
}
