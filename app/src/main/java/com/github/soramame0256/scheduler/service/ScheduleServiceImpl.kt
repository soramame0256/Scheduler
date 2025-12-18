package com.github.soramame0256.scheduler.service

import com.github.soramame0256.scheduler.model.Schedule
import com.github.soramame0256.scheduler.model.Time
import com.github.soramame0256.scheduler.model.TimeRange
import com.github.soramame0256.scheduler.model.Weekday
import com.github.soramame0256.scheduler.model.exception.ScheduleNotFoundException
import com.github.soramame0256.scheduler.repository.ScheduleDao
import com.github.soramame0256.scheduler.repository.mapper.TimeRangeMapper
import com.github.soramame0256.scheduler.repository.mapper.ScheduleMapper

/**
 * このクラスはスケジュールの読み取りと書き込みの両方の操作を実装します。
 */
class ScheduleServiceImpl(private val dao: ScheduleDao) : ScheduleService {
    override suspend fun getSchedules(): List<Schedule> = dao.getSchedulesWithTime().map { ScheduleMapper.toDomain(it) }

    override suspend fun getSchedulesAtTime(time: Time): List<Schedule> = dao.getSchedulesAtTime(time).map { ScheduleMapper.toDomain(it) }

    override suspend fun insertSchedule(schedule: Schedule): Long = dao.insertSchedule(ScheduleMapper.toEntity(schedule))

    override suspend fun updateSchedule(schedule: Schedule) = dao.updateSchedule(ScheduleMapper.toEntity(schedule))

    override suspend fun deleteSchedule(schedule: Schedule) = dao.deleteSchedule(ScheduleMapper.toEntity(schedule))

    override suspend fun getAllTimeRanges(): List<TimeRange> = dao.getAllTimeRanges().map { TimeRangeMapper.toDomain(it) }

    override suspend fun getScheduleAtTimeAndWeekday(time: Time, weekday: Weekday): Result<Schedule> {
        val schedule = dao.getScheduleAtTimeAndWeekday(time, weekday)
        return if (schedule != null) {
            Result.success(ScheduleMapper.toDomain(schedule))
        } else {
            Result.failure(ScheduleNotFoundException())
        }
    }

    override suspend fun insertTimeRange(startTime: Time, endTime: Time): TimeRange {
        val id = dao.insertTimeRange(TimeRangeMapper.toEntity(TimeRange(startTime = startTime, endTime = endTime)))
        return TimeRange(id, startTime, endTime)
    }

    override suspend fun updateTimeRange(timeRange: TimeRange) = dao.updateTimeRange(TimeRangeMapper.toEntity(timeRange))

    override suspend fun deleteTimeRange(timeRange: TimeRange) = dao.deleteTimeRange(TimeRangeMapper.toEntity(timeRange))
}
