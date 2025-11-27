package com.github.soramame0256.scheduler.backend.schedule.db

import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.Schedule
import java.util.concurrent.CompletableFuture

/**
 * スケジュールの書き込み操作を定義するインターフェイスです。
 */
interface ScheduleWriter {
    fun insertSchedule(schedule: Schedule): CompletableFuture<Void>
    fun updateSchedule(schedule: Schedule): CompletableFuture<Void>
}
