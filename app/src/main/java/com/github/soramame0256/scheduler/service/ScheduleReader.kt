package com.github.soramame0256.scheduler.service

import com.github.soramame0256.scheduler.domain.Time
import com.github.soramame0256.scheduler.domain.Weekday
import com.github.soramame0256.scheduler.repository.entity.ScheduleEntity
import com.github.soramame0256.scheduler.repository.joinedentity.CombinedSchedule

/**
 * スケジュールの読み取り操作を定義するインターフェイスです。
 */
interface ScheduleReader {
    suspend fun getSchedules(): List<CombinedSchedule>
    suspend fun getSchedulesAtTime(time: Time): List<CombinedSchedule>
    suspend fun getScheduleAtTimeAndWeekday(time: Time, weekday: Weekday): Result<ScheduleEntity>
}
