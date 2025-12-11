package com.github.soramame0256.scheduler.backend.schedule.innerdb.joinedentity

import androidx.room.Embedded
import androidx.room.Relation
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.Schedule
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.TimeRange

data class CombinedSchedule(
    @Embedded val schedule: Schedule,
    @Relation(parentColumn = "timeRangeId", entityColumn = "timetableId") val timeRange: TimeRange
)
