package com.github.soramame0256.scheduler.ui

import android.os.Bundle
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
    lateinit var service: ScheduleService

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
                if (!timeValidator.matcher(startTimeString).matches() || !timeValidator.matcher(endTimeString).matches()) {
                    showToast(R.string.invalidTimeRange)
                    return@setOnClickListener
                }
                val startTimeParts = startTimeString.split(":")
                val endTimeParts = endTimeString.split(":")

                if (startTimeParts.size != 2 || endTimeParts.size != 2) {
                    showToast(R.string.invalidTimeRange)
                    return@setOnClickListener
                }
                
                val (startHour, startMinute) = parseTimeParts(startTimeParts) ?: run {
                    showToast(R.string.invalidInput)
                    return@setOnClickListener
                }
                
                val (endHour, endMinute) = parseTimeParts(endTimeParts) ?: run {
                    showToast(R.string.invalidInput)
                    return@setOnClickListener
                }
                val start = Time(startHour, startMinute)
                val end = Time(endHour, endMinute)
                lifecycleScope.launch {
                    val conflicts = service.getAllTimeRanges().filter {
                        start < it.endTime && it.startTime < end
                    }
                    if (conflicts.isEmpty()) {
                        service.insertTimeRange(start, end)
                        showToast(R.string.timeRangeInsertSuccess)
                    } else {
                        showToast(R.string.time_range_settings_conflict)
                    }
                    update()
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
                }
            }
            row.addView(deleteButton)
            table.addView(row)
        }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    private fun parseTimeParts(timeParts: List<String>): Pair<Int, Int>? {
        if (timeParts.size != 2) return null

        val hour = timeParts[0].toIntOrNull() ?: return null
        val minute = timeParts[1].toIntOrNull() ?: return null

        return Pair(hour, minute)
    }
    companion object {
        @JvmStatic
        val timeValidator: Pattern = Pattern.compile("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
    }
}