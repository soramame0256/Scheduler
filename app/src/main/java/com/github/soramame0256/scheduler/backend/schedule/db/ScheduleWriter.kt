package com.github.soramame0256.scheduler.backend.schedule.db

import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.Schedule
import java.util.concurrent.CompletableFuture

/**
 * スケジュールの書き込み操作を定義するインターフェイスです。
 */
interface ScheduleWriter {
    suspend fun insertSchedule(schedule: Schedule): Long
    suspend fun updateSchedule(schedule: Schedule)
    suspend fun deleteSchedule(schedule: Schedule)
}
