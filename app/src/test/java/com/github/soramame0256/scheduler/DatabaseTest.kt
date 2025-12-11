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
import junit.framework.TestCase.assertEquals
import org.junit.After
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
    fun writeAndRead() {
        val timeRange = TimeRange(start = Time(0, 25), end = Time(1, 12))
        val timeRange2 = TimeRange(start = Time(1,21), end = Time(2, 53))
        val schedule = Schedule(Weekday.MONDAY, 1, "test")
        val schedule2 = Schedule(Weekday.TUESDAY, 2, "test2")
        val model = ScheduleModel(dao)
        model.insertTimeRange(timeRange).thenCompose {
            model.insertTimeRange(timeRange2)
        }.thenCompose {
            model.insertSchedule(schedule)
        }.thenCompose {
            model.insertSchedule(schedule2)
        }.thenAccept {
            assertEquals("test", model.getSchedulesAtTime(Time(0, 26)).get()[0].schedule.msg)
            assertEquals("test2", model.getSchedulesAtTime(Time(1, 22)).get()[0].schedule.msg)
            assertEquals("test", model.getScheduleAtTimeAndWeekday(Time(0, 26),Weekday.MONDAY).get().getOrThrow().msg)
            assertEquals("test2", model.getScheduleAtTimeAndWeekday(Time(1, 22),Weekday.TUESDAY).get().getOrThrow().msg)
            assertEquals(true, model.getScheduleAtTimeAndWeekday(Time(1, 22),Weekday.WEDNESDAY).get().isFailure)
        }.thenCompose {
            model.deleteSchedule(schedule)
            model.deleteTimeRange(timeRange)
        }.thenAccept {
            assertEquals(1, model.getAllTimeRanges().get().size)
            assertEquals(1, model.getSchedules().get().size)
        }
    }
}