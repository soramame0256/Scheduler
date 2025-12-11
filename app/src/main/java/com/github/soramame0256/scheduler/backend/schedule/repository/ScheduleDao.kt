package com.github.soramame0256.scheduler.backend.schedule.repository

import androidx.room.*
import com.github.soramame0256.scheduler.backend.schedule.Time
import com.github.soramame0256.scheduler.backend.schedule.Weekday
import com.github.soramame0256.scheduler.backend.schedule.repository.entity.Schedule
import com.github.soramame0256.scheduler.backend.schedule.repository.entity.TimeRange
import com.github.soramame0256.scheduler.backend.schedule.repository.joinedentity.CombinedSchedule

@Dao
interface ScheduleDao {
    @Transaction
    @Query("SELECT * FROM schedule")
    suspend fun getSchedulesWithTime(): List<CombinedSchedule>

    @Query("SELECT * FROM time_range")
    suspend fun getAllTimeRanges(): List<TimeRange>

    @Transaction
    @Query("SELECT * FROM schedule INNER JOIN time_range ON schedule.timetableId = time_range.timetableId WHERE time_range.start <= :time and time_range.endTime >= :time ORDER BY time_range.start ASC")
    suspend fun getSchedulesAtTime(time: Time): List<CombinedSchedule>

    @Insert
    suspend fun insertSchedule(schedule: Schedule): Long

    @Insert
    suspend fun insertTimeRange(timeRange: TimeRange): Long

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Transaction
    @Query("SELECT * FROM schedule INNER JOIN time_range ON schedule.timetableId = time_range.timetableId WHERE schedule.weekday = :weekday and time_range.start <= :time and time_range.endTime >= :time ORDER BY time_range.start ASC LIMIT 1")
    suspend fun getScheduleAtTimeAndWeekday(weekday: Weekday, time: Time): CombinedSchedule?

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteTimeRange(timeRange: TimeRange)

    @Update
    suspend fun updateTimeRange(timeRange: TimeRange)
}
