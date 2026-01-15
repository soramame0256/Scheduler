package com.github.soramame0256.scheduler.model

import android.util.Log
import com.github.soramame0256.scheduler.BuildConfig

enum class Weekday(val value: Int) {
    SUNDAY(0),
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    ERROR(-1);

    companion object {
        private val map = entries.associateBy(Weekday::value)
        fun fromValue(value: Int): Weekday = map[value] ?: if (BuildConfig.DEBUG) throw IllegalArgumentException("Invalid Weekday value: $value") else run {
            Log.e("Weekday", "不明な値が入力されました: value = $value")
            ERROR
        }
    }
}
