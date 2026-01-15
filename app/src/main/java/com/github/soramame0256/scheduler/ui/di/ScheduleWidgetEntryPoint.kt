package com.github.soramame0256.scheduler.ui.di

import com.github.soramame0256.scheduler.service.ScheduleService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ScheduleWidgetEntryPoint {
    fun scheduleService(): ScheduleService
}