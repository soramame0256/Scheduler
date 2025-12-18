package com.github.soramame0256.scheduler.repository.mapper

import com.github.soramame0256.scheduler.domain.Schedule
import com.github.soramame0256.scheduler.repository.entity.ScheduleEntity
import com.github.soramame0256.scheduler.repository.joinedentity.CombinedSchedule

object ScheduleMapper {
    fun toDomain(combinedSchedule: CombinedSchedule) = Schedule(weekday = combinedSchedule.schedule.weekday, timeRange = TimeRangeMapper.toDomain(combinedSchedule.timeRange), message = combinedSchedule.schedule.message)

    fun toEntity(schedule: Schedule) = ScheduleEntity(weekday = schedule.weekday, timetableId = schedule.timeRange.id, message = schedule.message)
}
