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
    @Query("SELECT * FROM Schedule")
    suspend fun getSchedulesWithTime(): List<CombinedSchedule>

    @Query("SELECT * FROM TimeRange")
    suspend fun getAllTimeRanges(): List<TimeRange>

    @Transaction
    @Query("SELECT * FROM Schedule INNER JOIN TimeRange ON Schedule.timetableId = TimeRange.timetableId WHERE TimeRange.start <= :time and TimeRange.endTime >= :time ORDER BY TimeRange.start ASC")
    suspend fun getSchedulesAtTime(time: Time): List<CombinedSchedule>

    @Insert
    suspend fun insertSchedule(schedule: Schedule): Long

    @Insert
    suspend fun insertTimeRange(timeRange: TimeRange): Long

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Transaction
    @Query("SELECT * FROM Schedule INNER JOIN TimeRange ON Schedule.timetableId = TimeRange.timetableId WHERE Schedule.weekday = :weekday and TimeRange.start <= :time and TimeRange.endTime >= :time ORDER BY TimeRange.start ASC LIMIT 1")
    suspend fun getSchedulesAtTimeAndWeekday(weekday: Weekday, time: Time): CombinedSchedule?

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteTimeRange(timeRange: TimeRange)

    @Update
    suspend fun updateTimeRange(timeRange: TimeRange)
}
