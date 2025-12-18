package com.github.soramame0256.scheduler.service.di

import com.github.soramame0256.scheduler.service.ScheduleService
import com.github.soramame0256.scheduler.service.ScheduleServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    abstract fun bindScheduleService(scheduleServiceImpl: ScheduleServiceImpl): ScheduleService
}
