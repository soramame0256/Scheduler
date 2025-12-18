package com.github.soramame0256.scheduler.repository.mapper

import com.github.soramame0256.scheduler.model.TimeRange
import com.github.soramame0256.scheduler.repository.entity.TimeRangeEntity

interface TimeRangeMapper {
    fun toEntity(domain: TimeRange): TimeRangeEntity
    fun toDomain(entity: TimeRangeEntity): TimeRange
}