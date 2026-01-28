package com.github.soramame0256.scheduler.model

data class Time(val hour: Int, val minute: Int): Comparable<Time> {
    init {
        require(hour in 0..23) { "hour must be between 0 and 23" }
        require(minute in 0..59) { "minute must be between 0 and 59" }
    }

    fun toIntegerRepresentation() = hour * 100 + minute
    override fun toString() = "%02d:%02d".format(hour, minute)
    override fun compareTo(other: Time): Int = this.toIntegerRepresentation().compareTo(other.toIntegerRepresentation())
}
