package com.github.soramame0256.scheduler.service

import com.github.soramame0256.scheduler.model.Schedule
import com.github.soramame0256.scheduler.model.Time
import com.github.soramame0256.scheduler.model.TimeRange
import com.github.soramame0256.scheduler.model.Weekday
import com.github.soramame0256.scheduler.model.exception.ScheduleNotFoundException
import com.github.soramame0256.scheduler.repository.ScheduleDao
import com.github.soramame0256.scheduler.repository.mapper.ScheduleMapper
import com.github.soramame0256.scheduler.repository.mapper.TimeRangeMapper
import javax.inject.Inject
import javax.inject.Singleton

/**
 * このクラスはスケジュールの読み取りと書き込みの両方の操作を実装します。
 */

@Singleton
class ScheduleServiceImpl @Inject constructor(
    private val dao: ScheduleDao,
    private val scheduleMapper: ScheduleMapper,
    private val timeRangeMapper: TimeRangeMapper
) : ScheduleService {
    override suspend fun getSchedules(): List<Schedule> = dao.getSchedulesWithTime().map { scheduleMapper.toDomain(it) }

    override suspend fun getSchedulesAtTime(time: Time): List<Schedule> = dao.getSchedulesAtTime(time).map { scheduleMapper.toDomain(it) }

    override suspend fun insertSchedule(schedule: Schedule): Long = dao.insertSchedule(scheduleMapper.toEntity(schedule))

    override suspend fun updateSchedule(schedule: Schedule) = dao.updateSchedule(scheduleMapper.toEntity(schedule))

    override suspend fun deleteSchedule(schedule: Schedule) = dao.deleteSchedule(scheduleMapper.toEntity(schedule))

    override suspend fun getAllTimeRanges(): List<TimeRange> = dao.getAllTimeRanges().map { timeRangeMapper.toDomain(it) }

    override suspend fun getScheduleAtTimeAndWeekday(time: Time, weekday: Weekday): Result<Schedule> {
        val schedule = dao.getScheduleAtTimeAndWeekday(time, weekday)
        return if (schedule != null) {
            Result.success(scheduleMapper.toDomain(schedule))
        } else {
            Result.failure(ScheduleNotFoundException())
        }
    }

    override suspend fun getNextScheduleAtTimeAndWeekday(time: Time, weekday: Weekday): Result<Schedule> {
        val nextSchedule = dao.getNextScheduleAtTimeAndWeekday(time, weekday)
        return if (nextSchedule != null) {
            Result.success(scheduleMapper.toDomain(nextSchedule))
        } else {
            Result.failure(ScheduleNotFoundException())
        }

    }

    override suspend fun insertTimeRange(startTime: Time, endTime: Time): TimeRange {
        val id = dao.insertTimeRange(timeRangeMapper.toEntity(TimeRange(startTime = startTime, endTime = endTime)))
        return TimeRange(id, startTime, endTime)
    }

    override suspend fun updateTimeRange(timeRange: TimeRange) = dao.updateTimeRange(timeRangeMapper.toEntity(timeRange))

    override suspend fun deleteTimeRange(timeRange: TimeRange) = dao.deleteTimeRange(timeRangeMapper.toEntity(timeRange))
}
