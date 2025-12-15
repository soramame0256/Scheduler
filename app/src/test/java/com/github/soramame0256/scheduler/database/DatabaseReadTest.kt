package com.github.soramame0256.scheduler.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.soramame0256.scheduler.domain.Schedule
import com.github.soramame0256.scheduler.domain.Time
import com.github.soramame0256.scheduler.domain.Weekday
import com.github.soramame0256.scheduler.domain.TimeRange
import com.github.soramame0256.scheduler.repository.AppDatabase
import com.github.soramame0256.scheduler.service.ScheduleService
import com.github.soramame0256.scheduler.service.ScheduleServiceImpl
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseReadTest {
    private lateinit var db: AppDatabase
    private lateinit var service: ScheduleService

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val dao = db.scheduleDao()
        service = ScheduleServiceImpl(dao)
        insert()
    }
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
    private fun insert() = runBlocking {
        val insertedTimeRangeWithId = service.insertTimeRange(Time(0, 25), Time(1, 12))
        val insertedTimeRangeWithId2 = service.insertTimeRange(Time(1, 21), Time(2, 53))
        val schedule = Schedule(Weekday.MONDAY, insertedTimeRangeWithId, "test")
        val schedule2 = Schedule(Weekday.TUESDAY, insertedTimeRangeWithId2, "test2")
        service.insertSchedule(schedule)
        service.insertSchedule(schedule2)

    }
    @Test
    fun read() = runBlocking {
        assertEquals("test", service.getScheduleAtTimeAndWeekday(Time(0, 26), Weekday.MONDAY).getOrThrow().message)
        assertEquals("test2", service.getScheduleAtTimeAndWeekday(Time(1, 22), Weekday.TUESDAY).getOrThrow().message)
        assertEquals(true, service.getScheduleAtTimeAndWeekday(Time(1, 22), Weekday.WEDNESDAY).isFailure)
    }
}
