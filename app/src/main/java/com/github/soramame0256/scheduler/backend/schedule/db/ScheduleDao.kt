package com.github.soramame0256.scheduler.backend.schedule.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.soramame0256.scheduler.backend.schedule.db.entity.Schedule
import com.github.soramame0256.scheduler.backend.schedule.db.entity.TimeRange
import com.github.soramame0256.scheduler.backend.schedule.db.joinedentity.CombinedSchedule

@Dao
interface ScheduleDao {
    @Transaction
    @Query("SELECT * FROM Schedule")
    suspend fun getSchedulesWithTime(): List<CombinedSchedule>

    @Query("SELECT * FROM TimeRange")
    suspend fun getAllTimeRanges(): List<TimeRange>

    @Query("SELECT * FROM Schedule, TimeRange WHERE TimeRange.start <= :time and TimeRange.`end` >= :time and Schedule.timeRange = TimeRange.timetableId")
    suspend fun getSchedulesAtTime(time: Int): List<CombinedSchedule>

    @Insert(entity = Schedule::class)
    suspend fun insertSchedule(schedule: Schedule)

    @Insert(entity = TimeRange::class)
    suspend fun insertTimeRange(timeRange: TimeRange)

    @Update(entity = Schedule::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSchedule(schedule: Schedule)
}