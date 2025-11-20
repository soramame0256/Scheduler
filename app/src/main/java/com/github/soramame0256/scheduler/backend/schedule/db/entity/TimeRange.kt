package com.github.soramame0256.scheduler.backend.schedule.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 時間枠を表すデータクラス
 */
@Entity(tableName = "TimeRange")
data class TimeRange(
    @PrimaryKey(true) val timetableId: Int = 0,
    @ColumnInfo("start") val start: Int,
    @ColumnInfo("end") val end: Int
) {
    init {
        // validation
        if (start > end) throw IllegalArgumentException("start must be less than end")
    }
}