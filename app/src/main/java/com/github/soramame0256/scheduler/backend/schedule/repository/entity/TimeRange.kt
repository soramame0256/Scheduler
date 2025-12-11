package com.github.soramame0256.scheduler.backend.schedule.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.soramame0256.scheduler.backend.schedule.Time

/**
 * 時間枠を表すデータクラス
 */
@Entity(tableName = "time_range")
data class TimeRange(
    @PrimaryKey(true) val timetableId: Long = 0,
    @ColumnInfo("start") val start: Time,
    @ColumnInfo("endTime") val endTime: Time
) {
    init {
        // validation
        if (start > endTime) throw IllegalArgumentException("start must not be after end")
    }
}
