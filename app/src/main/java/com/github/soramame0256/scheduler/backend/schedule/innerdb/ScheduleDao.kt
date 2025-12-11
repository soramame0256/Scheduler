package com.github.soramame0256.scheduler.backend.schedule.innerdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.soramame0256.scheduler.backend.schedule.Time
import com.github.soramame0256.scheduler.backend.schedule.Weekday
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.Schedule
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.TimeRange
import com.github.soramame0256.scheduler.backend.schedule.innerdb.joinedentity.CombinedSchedule

@Dao
interface ScheduleDao {
    @Transaction
    @Query("SELECT * FROM Schedule")
    suspend fun getSchedulesWithTime(): List<CombinedSchedule>

    @Query("SELECT * FROM TimeRange")
    suspend fun getAllTimeRanges(): List<TimeRange>

    @Transaction
    @Query("SELECT * FROM Schedule, TimeRange WHERE TimeRange.start <= :time and TimeRange.endTime >= :time and Schedule.timeRange = TimeRange.timetableId")
    suspend fun getSchedulesAtTime(time: Time): List<CombinedSchedule>

    @Insert
    suspend fun insertSchedule(schedule: Schedule): Long

    @Insert
    suspend fun insertTimeRange(timeRange: TimeRange): Long

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Query("SELECT * FROM Schedule, TimeRange WHERE Schedule.weekday = :weekday and Schedule.timeRange = TimeRange.timetableId and TimeRange.start <= :time and TimeRange.endTime >= :time")
    suspend fun getSchedulesAtTimeAndWeekday(weekday: Weekday, time: Time): List<CombinedSchedule>

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)
    @Delete
    suspend fun deleteTimeRange(timeRange: TimeRange)
    @Update
    suspend fun updateTimeRange(timeRange: TimeRange)
}