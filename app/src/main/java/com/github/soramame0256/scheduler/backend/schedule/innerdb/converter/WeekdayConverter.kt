package com.github.soramame0256.scheduler.backend.schedule.innerdb.converter

import androidx.room.TypeConverter
import com.github.soramame0256.scheduler.backend.schedule.Weekday

class WeekdayConverter {
    @TypeConverter
    fun toDbValue(weekday: Weekday) : Int? = weekday.value
    @TypeConverter
    fun fromDbValue(i: Int?) : Weekday? = i?.let { Weekday.entries[it] }
}