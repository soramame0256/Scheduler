package com.github.soramame0256.scheduler.backend.schedule.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.github.soramame0256.scheduler.backend.schedule.db.entity.TimeRange

@Entity(tableName = "Schedule",
    foreignKeys = [ForeignKey(
        entity = TimeRange::class,
        parentColumns = ["timetableId"],
        childColumns = ["timeRange"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Schedule(
    @PrimaryKey(true) val scheduleId: Int = 0,
    @ColumnInfo("weekday") val weekday: Int,
    @ColumnInfo("timeRange") val timeRangeId: Int,
    @ColumnInfo("message") val msg: String
)