package com.github.soramame0256.scheduler.domain

data class Schedule(val message: String, val weekday: Weekday, val timeRange: TimeRange)
