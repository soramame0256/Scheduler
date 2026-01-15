package com.github.soramame0256.scheduler.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.github.soramame0256.scheduler.R
import com.github.soramame0256.scheduler.model.Time
import com.github.soramame0256.scheduler.model.Weekday
import com.github.soramame0256.scheduler.service.ScheduleService
import com.github.soramame0256.scheduler.ui.di.ScheduleWidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime

class ScheduleWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            ScheduleWidgetEntryPoint::class.java
        )
        val service = hiltEntryPoint.scheduleService()
        // そのままrunBlockingで処理するとメインスレッドが死ぬのでAppWidgetProvider.goAsync
        // を使用してBroadcastReceiverを延長してからCoroutineで処理
        val pendingResult = goAsync()
        coroutineScope.launch {
            try {
                appWidgetIds.map { id -> async { updateAppWidget(context, appWidgetManager, service, id) } }.awaitAll()
            } finally {
                pendingResult.finish()
            }
        }
    }
    private suspend fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, service: ScheduleService, appWidgetId: Int) {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val hour = now.hour
        val minute = now.minute
        val weekday = Weekday.from(now.dayOfWeek)
        val time = Time(hour, minute)
        val schedule = service.getScheduleAtTimeAndWeekday(time, weekday)
        val views = RemoteViews(context.packageName, R.layout.schedule_widget)
        val message = schedule.fold(
            onSuccess = { it.message },
            onFailure = { context.getString(R.string.no_schedule) }
        )
        views.setTextViewText(R.id.appwidget_text2, message)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
    companion object {
        private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    }
}