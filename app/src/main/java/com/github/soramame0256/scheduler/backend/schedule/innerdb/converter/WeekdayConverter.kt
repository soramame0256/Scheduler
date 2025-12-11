package com.github.soramame0256.scheduler.backend.schedule.innerdb.converter

import androidx.room.TypeConverter
import com.github.soramame0256.scheduler.backend.schedule.Weekday

class WeekdayConverter {
    @TypeConverter
    fun toDbValue(weekday: Weekday) : Int? = weekday.value
    @TypeConverter
    fun fromDbValue(i: Int?) : Weekday? = i?.let {
        require(it in Weekday.entries.indices) { "データベースに無効な値が設定されています。: Weekday = $it. Expected 0 <= i < ${Weekday.entries.size}" }
        Weekday.entries[it]
    }
}
