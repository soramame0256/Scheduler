package com.github.soramame0256.scheduler.repository.mapper

import com.github.soramame0256.scheduler.model.Schedule
import com.github.soramame0256.scheduler.repository.entity.ScheduleEntity
import com.github.soramame0256.scheduler.repository.joinedentity.CombinedSchedule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleMapperImpl @Inject constructor(
    private val timeRangeMapper: TimeRangeMapper
) : ScheduleMapper {
    override fun toDomain(entity: CombinedSchedule) = Schedule(
        weekday = entity.schedule.weekday,
        timeRange = timeRangeMapper.toDomain(entity.timeRange),
        message = entity.schedule.message
    )

    override fun toEntity(domain: Schedule) = ScheduleEntity(
        weekday = domain.weekday,
        timetableId = domain.timeRange.id,
        message = domain.message
    )
}
