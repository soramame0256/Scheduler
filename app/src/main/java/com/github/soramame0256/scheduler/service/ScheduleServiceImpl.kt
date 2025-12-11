package com.github.soramame0256.scheduler.service

import com.github.soramame0256.scheduler.domain.Schedule
import com.github.soramame0256.scheduler.domain.Time
import com.github.soramame0256.scheduler.domain.TimeRange
import com.github.soramame0256.scheduler.domain.Weekday
import com.github.soramame0256.scheduler.domain.exception.ScheduleNotFoundException
import com.github.soramame0256.scheduler.repository.ScheduleDao
import com.github.soramame0256.scheduler.repository.entity.ScheduleEntity
import com.github.soramame0256.scheduler.repository.entity.TimeRangeEntity
import com.github.soramame0256.scheduler.repository.joinedentity.CombinedSchedule

/**
 * このクラスはスケジュールの読み取りと書き込みの両方の操作を実装します。
 */
class ScheduleServiceImpl(private val dao: ScheduleDao) : ScheduleService {
    override suspend fun getSchedules(): List<Schedule> = dao.getSchedulesWithTime().map { combinedEntityToSchedule(it) }

    override suspend fun getSchedulesAtTime(time: Time): List<Schedule> = dao.getSchedulesAtTime(time).map { combinedEntityToSchedule(it) }

    override suspend fun insertSchedule(schedule: Schedule): Long = dao.insertSchedule(scheduleToEntity(schedule))

    override suspend fun updateSchedule(schedule: Schedule) = dao.updateSchedule(scheduleToEntity(schedule))

    override suspend fun deleteSchedule(schedule: Schedule) = dao.deleteSchedule(scheduleToEntity(schedule))

    override suspend fun getAllTimeRanges(): List<TimeRange> = dao.getAllTimeRanges().map { entityToTimeRange(it) }

    override suspend fun getScheduleAtTimeAndWeekday(time: Time, weekday: Weekday): Result<Schedule> {
        val schedule = dao.getScheduleAtTimeAndWeekday(time, weekday)
        return if (schedule != null) {
            Result.success(combinedEntityToSchedule(schedule))
        } else {
            Result.failure(ScheduleNotFoundException())
        }
    }

    override suspend fun insertTimeRange(startTime: Time, endTime: Time): TimeRange {
        val id = dao.insertTimeRange(timeRangeToEntity(TimeRange(startTime = startTime, endTime = endTime)))
        return TimeRange(id, startTime, endTime)
    }

    override suspend fun updateTimeRange(timeRange: TimeRange) = dao.updateTimeRange(timeRangeToEntity(timeRange))

    override suspend fun deleteTimeRange(timeRange: TimeRange) = dao.deleteTimeRange(timeRangeToEntity(timeRange))

    /*
     * repositoryレイヤ―に変更があったらここを変えないといけない。
     * 設計的にどうなんだろう?
     */

    private fun entityToTimeRange(timeRangeEntity: TimeRangeEntity) =
        TimeRange(timeRangeEntity.timetableId, timeRangeEntity.start, timeRangeEntity.endTime)

    private fun timeRangeToEntity(timeRange: TimeRange) = TimeRangeEntity(timeRange.id, timeRange.startTime, timeRange.endTime)

    private fun combinedEntityToSchedule(combinedSchedule: CombinedSchedule) = Schedule(combinedSchedule.schedule.weekday, entityToTimeRange(combinedSchedule.timeRange), combinedSchedule.schedule.message)

    private fun scheduleToEntity(schedule: Schedule) = ScheduleEntity(schedule.weekday, schedule.timeRange.id, schedule.message)
}
