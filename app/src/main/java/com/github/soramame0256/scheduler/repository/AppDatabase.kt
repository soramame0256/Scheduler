package com.github.soramame0256.scheduler.repository

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.soramame0256.scheduler.repository.converter.TimeConverter
import com.github.soramame0256.scheduler.repository.converter.WeekdayConverter
import com.github.soramame0256.scheduler.repository.entity.ScheduleEntity
import com.github.soramame0256.scheduler.repository.entity.TimeRangeEntity

@Database(entities = [ScheduleEntity::class, TimeRangeEntity::class], version = 2, exportSchema = true, autoMigrations = [AutoMigration(
    from = 1,
    to = 2
)])
@TypeConverters(TimeConverter::class, WeekdayConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}
