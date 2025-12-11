package com.github.soramame0256.scheduler.backend.schedule.db

import com.github.soramame0256.scheduler.backend.schedule.Time
import com.github.soramame0256.scheduler.backend.schedule.Weekday
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.Schedule
import com.github.soramame0256.scheduler.backend.schedule.innerdb.joinedentity.CombinedSchedule

/**
 * スケジュールの読み取り操作を定義するインターフェイスです。
 */
interface ScheduleReader {
    suspend fun getSchedules(): List<CombinedSchedule>
    suspend fun getSchedulesAtTime(time: Time): List<CombinedSchedule>
    suspend fun getScheduleAtTimeAndWeekday(time: Time, weekday: Weekday): Result<Schedule>
}
