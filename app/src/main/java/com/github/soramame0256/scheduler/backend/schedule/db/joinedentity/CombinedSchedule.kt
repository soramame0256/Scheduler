package com.github.soramame0256.scheduler.backend.schedule.db.joinedentity

import androidx.room.Embedded
import androidx.room.Relation
import com.github.soramame0256.scheduler.backend.schedule.db.entity.Schedule
import com.github.soramame0256.scheduler.backend.schedule.db.entity.TimeRange

data class CombinedSchedule(
    @Embedded val schedule: Schedule,
    @Relation(parentColumn = "timeRange", entityColumn = "timetableId") val timeRange: TimeRange
)