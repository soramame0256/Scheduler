package com.github.soramame0256.scheduler.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.github.soramame0256.scheduler.model.Weekday

@Entity(
    tableName = "schedule",
    foreignKeys = [ForeignKey(
        entity = TimeRangeEntity::class,
        parentColumns = ["timetableId"],
        childColumns = ["timetableId"],
        onDelete = ForeignKey.CASCADE
    )],
    primaryKeys = ["weekday", "timetableId"],
    indices = [Index(value = ["timetableId"])]
)
data class ScheduleEntity(
    @ColumnInfo("weekday") val weekday: Weekday,
    @ColumnInfo("timetableId") val timetableId: Long,
    @ColumnInfo("message") val message: String
)
