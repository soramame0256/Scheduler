package com.github.soramame0256.scheduler.backend.schedule.db

import com.github.soramame0256.scheduler.backend.schedule.Time
import com.github.soramame0256.scheduler.backend.schedule.Weekday
import com.github.soramame0256.scheduler.backend.schedule.innerdb.ScheduleDao
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.Schedule
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.TimeRange
import com.github.soramame0256.scheduler.backend.schedule.innerdb.joinedentity.CombinedSchedule
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture

/**
 * daoの関数をCompletableFutureを使用したわかりやすいものに変換して返します。
 * このクラスはスケジュールの読み取りと書き込みの両方の操作を実装します。
 */
class ScheduleModel(private val dao: ScheduleDao) : ScheduleReader, ScheduleWriter, TimeRangeReader, TimeRangeWriter {
    override fun getSchedules(): CompletableFuture<List<CombinedSchedule>> = CompletableFuture.supplyAsync {
        runBlocking {
            dao.getSchedulesWithTime()
        }
    }

    override fun getSchedulesAtTime(time: Time): CompletableFuture<List<CombinedSchedule>> = CompletableFuture.supplyAsync {
        runBlocking {
            dao.getSchedulesAtTime(time)
        }
    }

    override fun insertSchedule(schedule: Schedule): CompletableFuture<Void> = CompletableFuture.runAsync {
        runBlocking {
            dao.insertSchedule(schedule)
        }
    }

    override fun updateSchedule(schedule: Schedule): CompletableFuture<Void> = CompletableFuture.runAsync {
        runBlocking {
            dao.updateSchedule(schedule)
        }
    }

    override fun deleteSchedule(schedule: Schedule): CompletableFuture<Void> = CompletableFuture.runAsync {
        runBlocking {
            dao.deleteSchedule(schedule)
        }
    }

    override fun getAllTimeRanges(): CompletableFuture<List<TimeRange>> = CompletableFuture.supplyAsync {
        runBlocking {
            dao.getAllTimeRanges()
        }
    }


    override fun getScheduleAtTimeAndWeekday(time: Time, weekday: Weekday): CompletableFuture<Result<Schedule>> = CompletableFuture.supplyAsync {
        runBlocking {
            dao.getSchedulesAtTimeAndWeekday(weekday, time).getOrNull(0)?.let { Result.success(it.schedule) } ?: Result.failure(Exception("no schedule"))
        }
    }

    override fun insertTimeRange(timeRange: TimeRange): CompletableFuture<Void> = CompletableFuture.runAsync {
        runBlocking {
            dao.insertTimeRange(timeRange)
        }
    }

    override fun updateTimeRange(timeRange: TimeRange): CompletableFuture<Void> = CompletableFuture.runAsync {
        runBlocking {
            dao.updateTimeRange(timeRange)
        }
    }

    override fun deleteTimeRange(timeRange: TimeRange): CompletableFuture<Void> = CompletableFuture.runAsync {
        runBlocking {
            dao.deleteTimeRange(timeRange)
        }
    }

}