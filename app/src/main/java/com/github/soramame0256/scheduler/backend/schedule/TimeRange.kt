package com.github.soramame0256.scheduler.backend.schedule

/**
 * 時間枠を表すデータクラス
 */
data class TimeRange(val start: Int, val end: Int) {
    init {
        // validation
        if (start > end) throw IllegalArgumentException("start must be less than end")
    }
}
