package com.github.soramame0256.scheduler.domain

data class Schedule(val weekday: Weekday, val timeRange: TimeRange, val message: String)
