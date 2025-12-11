package com.github.soramame0256.scheduler.backend.schedule.innerdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.soramame0256.scheduler.backend.schedule.innerdb.converter.TimeConverter
import com.github.soramame0256.scheduler.backend.schedule.innerdb.converter.WeekdayConverter
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.Schedule
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.TimeRange

@Database(entities = [Schedule::class, TimeRange::class], version = 1, exportSchema = true)
@TypeConverters(TimeConverter::class, WeekdayConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}