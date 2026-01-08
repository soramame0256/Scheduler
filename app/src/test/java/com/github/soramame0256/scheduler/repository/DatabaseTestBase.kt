package com.github.soramame0256.scheduler.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.soramame0256.scheduler.repository.mapper.ScheduleMapperImpl
import com.github.soramame0256.scheduler.repository.mapper.TimeRangeMapperImpl
import com.github.soramame0256.scheduler.service.ScheduleService
import com.github.soramame0256.scheduler.service.ScheduleServiceImpl
import org.junit.Before

open class DatabaseTestBase {
    protected lateinit var db: AppDatabase
    protected lateinit var service: ScheduleService

    @Before
    open fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val dao = db.scheduleDao()
        val timeRangeMapper = TimeRangeMapperImpl()
        val scheduleMapper = ScheduleMapperImpl(timeRangeMapper)
        service = ScheduleServiceImpl(dao,scheduleMapper,timeRangeMapper)
    }
}