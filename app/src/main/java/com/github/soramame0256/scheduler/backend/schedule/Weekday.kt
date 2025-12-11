package com.github.soramame0256.scheduler.backend.schedule

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
        fun fromValue(value: Int): Weekday? = map[value]
    }
}
