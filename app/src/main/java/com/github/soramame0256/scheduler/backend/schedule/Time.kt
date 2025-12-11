package com.github.soramame0256.scheduler.backend.schedule

data class Time(val hour: Int, val minute: Int) {
    init {
        require(hour in 0..23) { "hour must be between 0 and 23" }
        require(minute in 0..59) { "minute must be between 0 and 59" }
    }
    fun formattedInteger() = hour * 100 + minute
}
