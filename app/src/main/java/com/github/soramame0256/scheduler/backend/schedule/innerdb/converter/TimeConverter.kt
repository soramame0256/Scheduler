package com.github.soramame0256.scheduler.backend.schedule.innerdb.converter

import androidx.room.TypeConverter
import com.github.soramame0256.scheduler.backend.schedule.Time

class TimeConverter {
    @TypeConverter
    fun toDbValue(t: Time): Int = t.toIntegerRepresentation()

    @TypeConverter
    fun fromDbValue(i: Int?): Time? = i?.let { Time(it / 100, it % 100) }
}
