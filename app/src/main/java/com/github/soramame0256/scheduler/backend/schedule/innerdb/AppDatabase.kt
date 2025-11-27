package com.github.soramame0256.scheduler.backend.schedule.innerdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.Schedule
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.TimeRange

@Database(entities = [Schedule::class, TimeRange::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}