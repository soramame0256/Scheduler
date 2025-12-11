package com.github.soramame0256.scheduler

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.soramame0256.scheduler.backend.schedule.Time
import com.github.soramame0256.scheduler.backend.schedule.Weekday
import com.github.soramame0256.scheduler.backend.schedule.service.ScheduleServiceImpl
import com.github.soramame0256.scheduler.backend.schedule.repository.AppDatabase
import com.github.soramame0256.scheduler.backend.schedule.repository.ScheduleDao
import com.github.soramame0256.scheduler.backend.schedule.repository.entity.Schedule
import com.github.soramame0256.scheduler.backend.schedule.repository.entity.TimeRange
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: ScheduleDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.scheduleDao()
    }
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
    @Test
    @Throws(Exception::class)
    fun writeAndRead() = runBlocking {
        val timeRangeToInsert = TimeRange(start = Time(0, 25), endTime = Time(1, 12))
        val timeRange2ToInsert = TimeRange(start = Time(1, 21), endTime = Time(2, 53))

        val model = ScheduleServiceImpl(dao)

        val timeRangeId = model.insertTimeRange(timeRangeToInsert)
        val insertedTimeRange = timeRangeToInsert.copy(timetableId = timeRangeId)
        val timeRangeId2 = model.insertTimeRange(timeRange2ToInsert)
        val insertedTimeRange2 = timeRange2ToInsert.copy(timetableId = timeRangeId2)
        val schedule = Schedule(Weekday.MONDAY, timeRangeId, "test")
        val schedule2 = Schedule(Weekday.TUESDAY, timeRangeId2, "test2")

        model.insertSchedule(schedule)
        model.insertSchedule(schedule2)
        
        val schedulesAtTime1 = model.getSchedulesAtTime(Time(0, 26))
        assertEquals(1, schedulesAtTime1.size)
        assertEquals("test", schedulesAtTime1.first().schedule.message)

        val schedulesAtTime2 = model.getSchedulesAtTime(Time(1, 22))
        assertEquals(1, schedulesAtTime2.size)
        assertEquals("test2", schedulesAtTime2.first().schedule.message)
        assertEquals("test", model.getScheduleAtTimeAndWeekday(Time(0, 26), Weekday.MONDAY).getOrThrow().message)
        assertEquals("test2", model.getScheduleAtTimeAndWeekday(Time(1, 22), Weekday.TUESDAY).getOrThrow().message)
        assertEquals(true, model.getScheduleAtTimeAndWeekday(Time(1, 22), Weekday.WEDNESDAY).isFailure)

        model.deleteTimeRange(insertedTimeRange)
        
        assertEquals(1, model.getAllTimeRanges().size)
        assertEquals(1, model.getSchedules().size)
    }
}
