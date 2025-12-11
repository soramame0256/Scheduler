package com.github.soramame0256.scheduler.service

import com.github.soramame0256.scheduler.domain.Time
import com.github.soramame0256.scheduler.domain.Weekday
import com.github.soramame0256.scheduler.domain.exception.ScheduleNotFoundException
import com.github.soramame0256.scheduler.repository.ScheduleDao
import com.github.soramame0256.scheduler.repository.entity.ScheduleEntity
import com.github.soramame0256.scheduler.repository.entity.TimeRangeEntity
import com.github.soramame0256.scheduler.repository.joinedentity.CombinedSchedule

/**
 * daoの関数をコルーチンベースで扱いやすくラップして提供します。
 * このクラスはスケジュールの読み取りと書き込みの両方の操作を実装します。
 */
class ScheduleServiceImpl(private val dao: ScheduleDao) : ScheduleService {
    override suspend fun getSchedules(): List<CombinedSchedule> = dao.getSchedulesWithTime()

    override suspend fun getSchedulesAtTime(time: Time): List<CombinedSchedule> = dao.getSchedulesAtTime(time)

    override suspend fun insertSchedule(schedule: ScheduleEntity): Long = dao.insertSchedule(schedule)

    override suspend fun updateSchedule(schedule: ScheduleEntity) = dao.updateSchedule(schedule)

    override suspend fun deleteSchedule(schedule: ScheduleEntity) = dao.deleteSchedule(schedule)

    override suspend fun getAllTimeRanges(): List<TimeRangeEntity> = dao.getAllTimeRanges()

    override suspend fun getScheduleAtTimeAndWeekday(time: Time, weekday: Weekday): Result<ScheduleEntity> {
        val combinedSchedule = dao.getScheduleAtTimeAndWeekday(time, weekday)
        return if (combinedSchedule != null) {
            Result.success(combinedSchedule.schedule)
        } else {
            Result.failure(ScheduleNotFoundException())
        }
    }

    override suspend fun insertTimeRange(timeRange: TimeRangeEntity): Long = dao.insertTimeRange(timeRange)

    override suspend fun updateTimeRange(timeRange: TimeRangeEntity) = dao.updateTimeRange(timeRange)

    override suspend fun deleteTimeRange(timeRange: TimeRangeEntity) = dao.deleteTimeRange(timeRange)
}
