package com.github.soramame0256.scheduler.backend.schedule.innerdb.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.soramame0256.scheduler.backend.schedule.Time

/**
 * 時間枠を表すデータクラス
 */
@Entity(tableName = "TimeRange")
data class TimeRange(
    @PrimaryKey(true) val timetableId: Int = 0,
    @ColumnInfo("start") val start: Time,
    @ColumnInfo("end") val end: Time
) {
    init {
        // validation
        if (start.formattedInteger() > end.formattedInteger()) throw IllegalArgumentException("start must be less than end")
    }
}