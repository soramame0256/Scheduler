package com.github.soramame0256.scheduler.backend.schedule.innerdb.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.github.soramame0256.scheduler.backend.schedule.Weekday

@Entity(tableName = "Schedule",
    foreignKeys = [ForeignKey(
        entity = TimeRange::class,
        parentColumns = ["timetableId"],
        childColumns = ["timeRange"],
        onDelete = ForeignKey.CASCADE
    )],
    primaryKeys = ["weekday", "timeRange"]
)
data class Schedule(
    @ColumnInfo("weekday") val weekday: Weekday,
    @ColumnInfo("timeRange") val timeRangeId: Long,
    @ColumnInfo("message") val msg: String
)