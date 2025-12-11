package com.github.soramame0256.scheduler.backend.schedule.repository.converter

import android.util.Log
import androidx.room.TypeConverter
import com.github.soramame0256.scheduler.backend.schedule.Weekday

class WeekdayConverter {
    @TypeConverter
    fun toDbValue(weekday: Weekday): Int = weekday.value

    @TypeConverter
    fun fromDbValue(i: Int?): Weekday {
        val value = i ?: return Weekday.ERROR
        val weekday = Weekday.entries.firstOrNull { it.value == value }
        if (weekday == null) {
            Log.e("WeekdayConverter", "データベースに無効な値が設定されています。: Weekday = $value")
            return Weekday.ERROR
        }
        return weekday

    }
}
