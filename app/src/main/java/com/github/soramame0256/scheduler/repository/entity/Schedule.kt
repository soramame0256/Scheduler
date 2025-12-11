package com.github.soramame0256.scheduler.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.github.soramame0256.scheduler.domain.Weekday

@Entity(
    tableName = "schedule",
    foreignKeys = [ForeignKey(
        entity = TimeRange::class,
        parentColumns = ["timetableId"],
        childColumns = ["timetableId"],
        onDelete = ForeignKey.CASCADE
    )],
    primaryKeys = ["weekday", "timetableId"]
)
data class Schedule(
    @ColumnInfo("weekday") val weekday: Weekday,
    @ColumnInfo("timetableId") val timetableId: Long,
    @ColumnInfo("message") val message: String
)
