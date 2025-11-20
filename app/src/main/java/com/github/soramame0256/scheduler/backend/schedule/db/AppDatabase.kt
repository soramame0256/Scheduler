package com.github.soramame0256.scheduler.backend.schedule.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.soramame0256.scheduler.backend.schedule.db.entity.Schedule
import com.github.soramame0256.scheduler.backend.schedule.db.entity.TimeRange

@Database(entities = [Schedule::class, TimeRange::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}