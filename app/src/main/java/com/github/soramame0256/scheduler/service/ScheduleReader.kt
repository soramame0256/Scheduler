package com.github.soramame0256.scheduler.service

import com.github.soramame0256.scheduler.model.Schedule
import com.github.soramame0256.scheduler.model.Time
import com.github.soramame0256.scheduler.model.Weekday

/**
 * スケジュールの読み取り操作を定義するインターフェイスです。
 */
interface ScheduleReader {
    suspend fun getSchedules(): List<Schedule>
    suspend fun getSchedulesAtTime(time: Time): List<Schedule>
    suspend fun getScheduleAtTimeAndWeekday(time: Time, weekday: Weekday): Result<Schedule>
    suspend fun getNextScheduleAtTimeAndWeekday(time: Time, weekday: Weekday): Result<Schedule>
}
