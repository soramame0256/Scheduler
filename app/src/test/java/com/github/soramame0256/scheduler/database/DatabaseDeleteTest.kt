package com.github.soramame0256.scheduler.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.soramame0256.scheduler.backend.schedule.Time
import com.github.soramame0256.scheduler.backend.schedule.Weekday
import com.github.soramame0256.scheduler.backend.schedule.repository.AppDatabase
import com.github.soramame0256.scheduler.backend.schedule.repository.entity.Schedule
import com.github.soramame0256.scheduler.backend.schedule.repository.entity.TimeRange
import com.github.soramame0256.scheduler.backend.schedule.service.ScheduleService
import com.github.soramame0256.scheduler.backend.schedule.service.ScheduleServiceImpl
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseDeleteTest {
    private lateinit var db: AppDatabase
    private lateinit var service: ScheduleService
    private var insertedTimeRange: TimeRange? = null

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
    fun insert() = runBlocking {
        val timeRangeToInsert = TimeRange(start = Time(0, 25), endTime = Time(1, 12))
        val timeRange2ToInsert = TimeRange(start = Time(1, 21), endTime = Time(2, 53))
        val timeRangeId = service.insertTimeRange(timeRangeToInsert)
        insertedTimeRange = timeRangeToInsert.copy(timetableId = timeRangeId)
        val timeRangeId2 = service.insertTimeRange(timeRange2ToInsert)
        val schedule = Schedule(Weekday.MONDAY, timeRangeId, "test")
        val schedule2 = Schedule(Weekday.TUESDAY, timeRangeId2, "test2")

        service.insertSchedule(schedule)
        service.insertSchedule(schedule2)
    }
    @Test
    fun delete() = runBlocking {
        assert(insertedTimeRange != null)
        service.deleteTimeRange(insertedTimeRange!!)

        assertEquals(1, service.getAllTimeRanges().size)
        assertEquals(1, service.getSchedules().size)
    }
}
