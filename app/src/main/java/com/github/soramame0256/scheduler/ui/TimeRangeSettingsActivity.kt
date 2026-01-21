package com.github.soramame0256.scheduler.ui

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.soramame0256.scheduler.R
import com.github.soramame0256.scheduler.model.Time
import com.github.soramame0256.scheduler.service.ScheduleService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class TimeRangeSettingsActivity : AppCompatActivity() {
    @Inject
    private lateinit var service: ScheduleService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val editTextTimeStart = findViewById<EditText>(R.id.editTextTimeStart)
        val editTextTimeEnd = findViewById<EditText>(R.id.editTextTimeEnd)
        val addButton = findViewById<Button>(R.id.button)
        lifecycleScope.launch {
            update()
        }
        addButton.setOnClickListener {
            val startTimeString = editTextTimeStart.text.toString()
            val endTimeString = editTextTimeEnd.text.toString()

            if (startTimeString.isNotEmpty() && endTimeString.isNotEmpty()) {
                try {
                    if (!timeValidator.matcher(startTimeString).matches() || !timeValidator.matcher(endTimeString).matches()) throw Exception()
                    val startTimeParts = startTimeString.split(":")
                    val endTimeParts = endTimeString.split(":")
                    if (startTimeParts.size == 2 && endTimeParts.size == 2) {
                        val startHour = startTimeParts[0].toInt()
                        val startMinute = startTimeParts[1].toInt()
                        val endHour = endTimeParts[0].toInt()
                        val endMinute = endTimeParts[1].toInt()
                        val start = Time(startHour, startMinute)
                        val end = Time(endHour, endMinute)
                        // ボタンクリック時の処理だしまぁメインスレッドでもいいでしょう...
                        lifecycleScope.launch {
                            val conflicts = service.getAllTimeRanges().filter {
                                start < it.endTime && it.startTime < end
                            }
                            if (conflicts.isEmpty()) {
                                service.insertTimeRange(start, end)
                                Toast.makeText(this@TimeRangeSettingsActivity, R.string.timeRangeInsertSuccess, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@TimeRangeSettingsActivity, R.string.time_range_settings_conflict, Toast.LENGTH_SHORT).show()
                            }
                            update()
                        }
                    } else {
                        throw Exception()
                    }
                } catch (e: Exception) {
                    Log.e("TimeRangeSettingsActivity", "error thrown", e)
                    Toast.makeText(this, this.getText(R.string.invalidTimeRange), Toast.LENGTH_SHORT).show()
                }
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    @MainThread
    private suspend fun update() {
        val table = findViewById<TableLayout>(R.id.trrtablelayout)
        table.removeAllViews()
        val timeRanges = service.getAllTimeRanges()
        val row = TableRow(this)
        val textViewTimeRangeHeader = TextView(this)
        textViewTimeRangeHeader.text = getString(R.string.header_time_range)
        row.addView(textViewTimeRangeHeader)
        val deleteButtonHeader = TextView(this)
        deleteButtonHeader.text = getString(R.string.header_delete)
        row.addView(deleteButtonHeader)
        table.addView(row)
        timeRanges.forEach { timeRange ->
            val row = TableRow(this)
            val textViewTimeRange = TextView(this)
            textViewTimeRange.text = timeRange.toString()
            row.addView(textViewTimeRange)
            val deleteButton = Button(this)
            deleteButton.text = getString(R.string.header_delete)
            deleteButton.setOnClickListener {
                lifecycleScope.launch {
                    service.deleteTimeRange(timeRange)
                    update()
                    // TODO: ここでたまにAndroidRuntimeExceptionが出るので直す。
                }
            }
            row.addView(deleteButton)
            table.addView(row)
        }
    }
    companion object {
        @JvmStatic
        val timeValidator: Pattern = Pattern.compile("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
    }
}