package com.github.soramame0256.scheduler.backend.schedule

data class Time(val hour: Int, val minute: Int) {
    fun formattedInteger() = hour * 100 + minute
}
