package com.github.soramame0256.scheduler.repository.di

import com.github.soramame0256.scheduler.repository.mapper.ScheduleMapper
import com.github.soramame0256.scheduler.repository.mapper.ScheduleMapperImpl
import com.github.soramame0256.scheduler.repository.mapper.TimeRangeMapper
import com.github.soramame0256.scheduler.repository.mapper.TimeRangeMapperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MapperModule {
    @Binds
    @Singleton
    abstract fun bindScheduleMapper(scheduleMapperImpl: ScheduleMapperImpl): ScheduleMapper

    @Binds
    @Singleton
    abstract fun bindTimeRangeMapper(timeRangeMapperImpl: TimeRangeMapperImpl): TimeRangeMapper
}