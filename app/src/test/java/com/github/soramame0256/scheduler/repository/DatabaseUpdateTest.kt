package com.github.soramame0256.scheduler.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.soramame0256.scheduler.model.Schedule
import com.github.soramame0256.scheduler.model.Time
import com.github.soramame0256.scheduler.model.Weekday
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseUpdateTest : DatabaseTestBase() {

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun update() = runBlocking {
        val timeRange = service.insertTimeRange(Time(0, 25), Time(1, 12))
        val timeRange2 = service.insertTimeRange(Time(1, 21), Time(2, 53))
        val schedule = Schedule(Weekday.MONDAY, timeRange, "test")
        val schedule2 = Schedule(Weekday.TUESDAY, timeRange2, "test2")

        service.insertSchedule(schedule)
        service.insertSchedule(schedule2)
        service.updateSchedule(schedule.copy(message = "updated"))

        val schedules = service.getSchedules()
        assertEquals(2, schedules.size)
        assertEquals("updated", schedules.first { it.weekday == Weekday.MONDAY && it.timeRange.id == timeRange.id }.message)
        assertEquals("test2", schedules.first { it.weekday == Weekday.TUESDAY && it.timeRange.id == timeRange2.id }.message)
    }

    @Test
    @Throws(Exception::class)
    fun updateNonexistentSchedule() = runBlocking {
        val timeRange = service.insertTimeRange(Time(0, 25), Time(1, 12))
        val schedule = Schedule(Weekday.MONDAY, timeRange, "test")
        service.insertSchedule(schedule)

        service.updateSchedule(Schedule(Weekday.TUESDAY, timeRange, "updated"))

        assertEquals(listOf(schedule), service.getSchedules())
    }
}
