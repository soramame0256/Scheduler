package com.github.soramame0256.scheduler.backend.schedule.db

import com.github.soramame0256.scheduler.backend.schedule.Time
import com.github.soramame0256.scheduler.backend.schedule.Weekday
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.Schedule
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.TimeRange
import com.github.soramame0256.scheduler.backend.schedule.innerdb.joinedentity.CombinedSchedule
import java.util.concurrent.CompletableFuture

/**
 * スケジュールの読み取り操作を定義するインターフェイスです。
 */
interface ScheduleReader {
    fun getSchedules(): CompletableFuture<List<CombinedSchedule>>
    fun getSchedulesAtTime(time: Time): CompletableFuture<List<CombinedSchedule>>
    fun getScheduleAtTimeAndWeekday(time: Time, weekday: Weekday): CompletableFuture<Result<Schedule>>
}
