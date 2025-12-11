package com.github.soramame0256.scheduler.backend.schedule.db

interface ScheduleRepository: ScheduleReader, ScheduleWriter, TimeRangeReader, TimeRangeWriter
