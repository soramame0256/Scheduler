package com.github.soramame0256.scheduler.ui

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.github.soramame0256.scheduler.R
import com.github.soramame0256.scheduler.model.Time
import com.github.soramame0256.scheduler.model.Weekday
import com.github.soramame0256.scheduler.service.ScheduleService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleWidget : AppWidgetProvider() {
    @Inject
    lateinit var service: ScheduleService
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        appWidgetIds?: return
        // そのままrunBlockingで処理するとメインスレッドが死ぬのでAppWidgetProvider.goAsync
        // を使用してBroadcastReceiverを延長してからCoroutineで処理
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                appWidgetIds.forEach { id -> updateAppWidget(context, appWidgetManager, id) }
            } finally {
                pendingResult.finish()
            }
        }
    }
    private suspend fun updateAppWidget(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int) {
        context ?: return
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val time = Time(hour, minute)
        val weekday = Weekday.fromValue(cal.get(Calendar.DAY_OF_WEEK) - 1)
        val schedule = service.getScheduleAtTimeAndWeekday(time, weekday)
        val views = RemoteViews(context.packageName, R.layout.schedule_widget)
        val message = schedule.fold(
            onSuccess = { it.message },
            onFailure = { context.getString(R.string.no_schedule) }
        )
        views.setTextViewText(R.id.appwidget_text2, message)
        appWidgetManager?.updateAppWidget(appWidgetId, views)
    }
}