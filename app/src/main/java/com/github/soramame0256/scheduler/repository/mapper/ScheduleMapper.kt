package com.github.soramame0256.scheduler.repository.mapper

import com.github.soramame0256.scheduler.model.Schedule
import com.github.soramame0256.scheduler.repository.entity.ScheduleEntity
import com.github.soramame0256.scheduler.repository.joinedentity.CombinedSchedule

interface ScheduleMapper {
    fun toDomain(entity: CombinedSchedule): Schedule
    fun toEntity(domain: Schedule): ScheduleEntity
}
