package com.github.soramame0256.scheduler.backend.schedule.db

import com.github.soramame0256.scheduler.backend.schedule.Time
import com.github.soramame0256.scheduler.backend.schedule.Weekday
import com.github.soramame0256.scheduler.backend.schedule.exception.ScheduleNotFoundException
import com.github.soramame0256.scheduler.backend.schedule.innerdb.ScheduleDao
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.Schedule
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.TimeRange
import com.github.soramame0256.scheduler.backend.schedule.innerdb.joinedentity.CombinedSchedule

/**
 * daoの関数をコルーチンベースで扱いやすくラップして提供します。
 * このクラスはスケジュールの読み取りと書き込みの両方の操作を実装します。
 */
class ScheduleModel(private val dao: ScheduleDao) : ScheduleReader, ScheduleWriter, TimeRangeReader, TimeRangeWriter {
    override suspend fun getSchedules(): List<CombinedSchedule> = dao.getSchedulesWithTime()

    override suspend fun getSchedulesAtTime(time: Time): List<CombinedSchedule> = dao.getSchedulesAtTime(time)

    override suspend fun insertSchedule(schedule: Schedule): Long =
        dao.insertSchedule(schedule)

    override suspend fun updateSchedule(schedule: Schedule) =
        dao.updateSchedule(schedule)


    override suspend fun deleteSchedule(schedule: Schedule) =
        dao.deleteSchedule(schedule)


    override suspend fun getAllTimeRanges(): List<TimeRange> = dao.getAllTimeRanges()


    override suspend fun getScheduleAtTimeAndWeekday(time: Time, weekday: Weekday): Result<Schedule> {
        return dao.getSchedulesAtTimeAndWeekday(weekday, time)?.let { Result.success(it.schedule) }
            ?: Result.failure(ScheduleNotFoundException())
    }

    override suspend fun insertTimeRange(timeRange: TimeRange): Long =
        dao.insertTimeRange(timeRange)


    override suspend fun updateTimeRange(timeRange: TimeRange) =
        dao.updateTimeRange(timeRange)

    override suspend fun deleteTimeRange(timeRange: TimeRange) =
        dao.deleteTimeRange(timeRange)
}
