package com.github.soramame0256.scheduler

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.soramame0256.scheduler.backend.schedule.innerdb.AppDatabase
import com.github.soramame0256.scheduler.backend.schedule.innerdb.ScheduleDao
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.Schedule
import com.github.soramame0256.scheduler.backend.schedule.innerdb.entity.TimeRange
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
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
        runBlocking {
            val timeRange = TimeRange(start = 0, end = 24)
            val timeRange2 = TimeRange(start = 25, end = 128)
            dao.insertTimeRange(timeRange)
            dao.insertSchedule(Schedule(weekday = 0, timeRangeId = 1, msg = "test"))
            dao.insertTimeRange(timeRange2)
            dao.insertSchedule(Schedule(weekday = 0, timeRangeId = 2, msg = "aiueo"))
            assertEquals(2, dao.getSchedulesWithTime().size)
            assertEquals("test", dao.getSchedulesAtTime(0)[0].schedule.msg)
            assertEquals(128, dao.getAllTimeRanges()[1].end)
            assertEquals("aiueo", dao.getSchedulesAtTime(30)[0].schedule.msg)
            assertEquals(24, dao.getAllTimeRanges()[0].end)
        }
    }
}