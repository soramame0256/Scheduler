package com.github.soramame0256.scheduler.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.soramame0256.scheduler.R
import com.github.soramame0256.scheduler.model.Time
import com.github.soramame0256.scheduler.ui.di.ScheduleWidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking
import java.util.regex.Pattern


class TimeRangeSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            this.applicationContext,
            ScheduleWidgetEntryPoint::class.java
        )
        val service = hiltEntryPoint.scheduleService()
        setContentView(R.layout.settings_activity)
        val editTextTimeStart = findViewById<EditText>(R.id.editTextTimeStart)
        val editTextTimeEnd = findViewById<EditText>(R.id.editTextTimeEnd)
        val addButton = findViewById<Button>(R.id.button)
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
                        runBlocking {
                            val conflicts = service.getAllTimeRanges().filter {
                                start < it.endTime && it.startTime < end
                            }
                            if (conflicts.isEmpty()) {
                                service.insertTimeRange(start, end)
                                Toast.makeText(this@TimeRangeSettingsActivity, R.string.timeRangeInsertSuccess, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@TimeRangeSettingsActivity, R.string.time_range_settings_conflict, Toast.LENGTH_SHORT).show()
                            }
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
    private fun update() {

    }
    companion object {
        @JvmStatic
        val timeValidator: Pattern = Pattern.compile("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
    }
}