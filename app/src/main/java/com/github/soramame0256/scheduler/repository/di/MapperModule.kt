package com.github.soramame0256.scheduler.repository.di

import com.github.soramame0256.scheduler.repository.mapper.ScheduleMapper
import com.github.soramame0256.scheduler.repository.mapper.ScheduleMapperImpl
import com.github.soramame0256.scheduler.repository.mapper.TimeRangeMapper
import com.github.soramame0256.scheduler.repository.mapper.TimeRangeMapperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MapperModule {
    @Binds
    abstract fun bindScheduleMapper(scheduleMapperImpl: ScheduleMapperImpl): ScheduleMapper

    @Binds
    abstract fun bindTimeRangeMapper(timeRangeMapperImpl: TimeRangeMapperImpl): TimeRangeMapper
}
