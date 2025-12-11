package com.github.soramame0256.scheduler.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.soramame0256.scheduler.domain.Time
import com.github.soramame0256.scheduler.domain.Weekday
import com.github.soramame0256.scheduler.repository.AppDatabase
import com.github.soramame0256.scheduler.repository.entity.ScheduleEntity
import com.github.soramame0256.scheduler.repository.entity.TimeRangeEntity
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
class DatabaseInsertTest {
    private lateinit var db: AppDatabase
    private lateinit var service: ScheduleService

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val dao = db.scheduleDao()
        service = ScheduleServiceImpl(dao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insert() = runBlocking {
        val timeRangeToInsert = TimeRangeEntity(start = Time(0, 25), endTime = Time(1, 12))
        val timeRange2ToInsert = TimeRangeEntity(start = Time(1, 21), endTime = Time(2, 53))
        val timeRangeId = service.insertTimeRange(timeRangeToInsert)
        val timeRangeId2 = service.insertTimeRange(timeRange2ToInsert)
        val schedule = ScheduleEntity(Weekday.MONDAY, timeRangeId, "test")
        val schedule2 = ScheduleEntity(Weekday.TUESDAY, timeRangeId2, "test2")

        service.insertSchedule(schedule)
        service.insertSchedule(schedule2)

        val schedulesAtTime1 = service.getSchedulesAtTime(Time(0, 26))
        assertEquals(1, schedulesAtTime1.size)
        assertEquals("test", schedulesAtTime1.first().schedule.message)

        val schedulesAtTime2 = service.getSchedulesAtTime(Time(1, 22))
        assertEquals(1, schedulesAtTime2.size)
        assertEquals("test2", schedulesAtTime2.first().schedule.message)
    }
}
