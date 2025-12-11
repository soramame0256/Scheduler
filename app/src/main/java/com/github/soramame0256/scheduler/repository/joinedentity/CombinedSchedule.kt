package com.github.soramame0256.scheduler.repository.joinedentity

import androidx.room.Embedded
import androidx.room.Relation
import com.github.soramame0256.scheduler.repository.entity.Schedule
import com.github.soramame0256.scheduler.repository.entity.TimeRange

data class CombinedSchedule(
    @Embedded val schedule: Schedule,
    @Relation(parentColumn = "timetableId", entityColumn = "timetableId") val timeRange: TimeRange
)
