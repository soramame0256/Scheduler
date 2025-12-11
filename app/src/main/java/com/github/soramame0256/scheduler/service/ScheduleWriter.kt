package com.github.soramame0256.scheduler.service

import com.github.soramame0256.scheduler.repository.entity.ScheduleEntity

/**
 * スケジュールの書き込み操作を定義するインターフェイスです。
 */
interface ScheduleWriter {
    suspend fun insertSchedule(schedule: ScheduleEntity): Long
    suspend fun updateSchedule(schedule: ScheduleEntity)
    suspend fun deleteSchedule(schedule: ScheduleEntity)
}
