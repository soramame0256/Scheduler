package com.github.soramame0256.scheduler.repository.joinedentity

import androidx.room.Embedded
import androidx.room.Relation
import com.github.soramame0256.scheduler.repository.entity.ScheduleEntity
import com.github.soramame0256.scheduler.repository.entity.TimeRangeEntity

data class CombinedSchedule(
    @Embedded val schedule: ScheduleEntity,
    @Relation(parentColumn = "timetableId", entityColumn = "timetableId") val timeRange: TimeRangeEntity
)
