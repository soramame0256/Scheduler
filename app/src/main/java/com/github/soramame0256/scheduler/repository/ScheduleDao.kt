package com.github.soramame0256.scheduler.repository

import androidx.room.*
import com.github.soramame0256.scheduler.domain.Time
import com.github.soramame0256.scheduler.domain.Weekday
import com.github.soramame0256.scheduler.repository.entity.ScheduleEntity
import com.github.soramame0256.scheduler.repository.entity.TimeRangeEntity
import com.github.soramame0256.scheduler.repository.joinedentity.CombinedSchedule

@Dao
interface ScheduleDao {
    @Transaction
    @Query("SELECT * FROM schedule")
    suspend fun getSchedulesWithTime(): List<CombinedSchedule>

    @Query("SELECT * FROM time_range")
    suspend fun getAllTimeRanges(): List<TimeRangeEntity>

    @Transaction
    @Query("SELECT schedule.* FROM schedule INNER JOIN time_range ON schedule.timetableId = time_range.timetableId WHERE time_range.start <= :time and time_range.endTime >= :time ORDER BY time_range.start ASC")
    suspend fun getSchedulesAtTime(time: Time): List<CombinedSchedule>

    @Insert
    suspend fun insertSchedule(schedule: ScheduleEntity): Long

    @Insert
    suspend fun insertTimeRange(timeRange: TimeRangeEntity): Long

    @Update
    suspend fun updateSchedule(schedule: ScheduleEntity)

    @Transaction
    @Query("SELECT schedule.* FROM schedule INNER JOIN time_range ON schedule.timetableId = time_range.timetableId WHERE schedule.weekday = :weekday and time_range.start <= :time and time_range.endTime >= :time ORDER BY time_range.start ASC LIMIT 1")
    suspend fun getScheduleAtTimeAndWeekday(time: Time, weekday: Weekday): CombinedSchedule?

    @Delete
    suspend fun deleteSchedule(schedule: ScheduleEntity)

    @Delete
    suspend fun deleteTimeRange(timeRange: TimeRangeEntity)

    @Update
    suspend fun updateTimeRange(timeRange: TimeRangeEntity)

    @Query("SELECT * FROM time_range WHERE timetableId = :id")
    suspend fun getTimeRangeById(id: Long): TimeRangeEntity?
}
