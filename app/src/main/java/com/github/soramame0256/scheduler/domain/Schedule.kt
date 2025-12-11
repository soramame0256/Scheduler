package com.github.soramame0256.scheduler.domain

data class Schedule(val id: Long, val message: String, val timeRange: TimeRange, val weekday: Weekday)
