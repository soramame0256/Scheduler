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
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleWidget : AppWidgetProvider() {
    @Inject
    lateinit var service: ScheduleService
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        appWidgetIds?: return
        appWidgetIds.forEach { id -> updateAppWidget(context, appWidgetManager, id) }
    }
    private fun updateAppWidget(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int) {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val time = Time(hour, minute)
        val weekday = Weekday.fromValue(cal.get(Calendar.DAY_OF_WEEK) - 1)
        runBlocking {
            val schedule = service.getScheduleAtTimeAndWeekday(time, weekday)
            val views = RemoteViews(context!!.packageName, R.layout.schedule_widget)
            val message = schedule.fold(
                onSuccess = { it.message },
                onFailure = { "現在の予定はありません。" }
            )
            views.setTextViewText(R.id.appwidget_text2, message)
            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }

    }
}