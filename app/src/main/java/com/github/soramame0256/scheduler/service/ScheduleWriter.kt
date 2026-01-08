package com.github.soramame0256.scheduler.service

import com.github.soramame0256.scheduler.model.Schedule

/**
 * スケジュールの書き込み操作を定義するインターフェイスです。
 */
interface ScheduleWriter {
    suspend fun insertSchedule(schedule: Schedule): Long
    suspend fun updateSchedule(schedule: Schedule)
    suspend fun deleteSchedule(schedule: Schedule)
}
