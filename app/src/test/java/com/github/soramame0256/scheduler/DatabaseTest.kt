package com.github.soramame0256.scheduler

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.soramame0256.scheduler.backend.schedule.Time
import com.github.soramame0256.scheduler.backend.schedule.Weekday
import com.github.soramame0256.scheduler.backend.schedule.db.ScheduleModel
import com.github.soramame0256.scheduler.backend.schedule.innerdb.AppDatabase
import com.github.soramame0256.scheduler.backend.schedule.innerdb.ScheduleDao
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.Schedule
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.TimeRange
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
    @Volatile
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
        var timeRange = TimeRange(start = Time(0, 25), endTime = Time(1, 12))
        var timeRange2 = TimeRange(start = Time(1, 21), endTime = Time(2, 53))
        var schedule: Schedule
        var schedule2: Schedule
        val model = ScheduleModel(dao)

        val timeRangeId = model.insertTimeRange(timeRange)
        timeRange = timeRange.copy(timetableId = timeRangeId.toInt())
        val timeRangeId2 = model.insertTimeRange(timeRange2)
        timeRange2 = timeRange2.copy(timetableId = timeRangeId2.toInt())
        schedule = Schedule(Weekday.MONDAY, timeRangeId.toInt(), "test")
        schedule2 = Schedule(Weekday.TUESDAY, timeRangeId2.toInt(), "test2")

        model.insertSchedule(schedule)
        model.insertSchedule(schedule2)
        
        assertEquals("test", model.getSchedulesAtTime(Time(0, 26))[0].schedule.msg)
        assertEquals("test2", model.getSchedulesAtTime(Time(1, 22))[0].schedule.msg)
        assertEquals("test", model.getScheduleAtTimeAndWeekday(Time(0, 26), Weekday.MONDAY).getOrThrow().msg)
        assertEquals("test2", model.getScheduleAtTimeAndWeekday(Time(1, 22), Weekday.TUESDAY).getOrThrow().msg)
        assertEquals(true, model.getScheduleAtTimeAndWeekday(Time(1, 22), Weekday.WEDNESDAY).isFailure)
        
        model.deleteSchedule(schedule)
        model.deleteTimeRange(timeRange)
        
        assertEquals(1, model.getAllTimeRanges().size)
        assertEquals(1, model.getSchedules().size)
    }
}