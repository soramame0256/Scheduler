package com.github.soramame0256.scheduler.ui.timerange

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.soramame0256.scheduler.R
import com.github.soramame0256.scheduler.databinding.TimeRangeSettingsActivityBinding
import com.github.soramame0256.scheduler.model.Time
import com.github.soramame0256.scheduler.model.TimeRange
import com.github.soramame0256.scheduler.service.ScheduleService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class TimeRangeSettingsActivity : AppCompatActivity() {
    @Inject
    lateinit var service: ScheduleService
    private lateinit var binding: TimeRangeSettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TimeRangeSettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val editTextTimeStart = binding.editTextTimeStart
        val editTextTimeEnd = binding.editTextTimeEnd
        val addButton = binding.button
        lifecycleScope.launch {
            update()
        }
        addButton.setOnClickListener {
            val startTimeString = editTextTimeStart.text.toString()
            val endTimeString = editTextTimeEnd.text.toString()

            if (startTimeString.isNotEmpty() && endTimeString.isNotEmpty()) {
                if (!timeValidator.matcher(startTimeString).matches() || !timeValidator.matcher(endTimeString).matches()) {
                    showToast(R.string.invalidInput)
                    return@setOnClickListener
                }
                val startTimeParts = startTimeString.split(":")
                val endTimeParts = endTimeString.split(":")

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
                        withContext(Dispatchers.IO) {
                            service.insertTimeRange(start, end)
                        }
                        showToast(R.string.timeRangeInsertSuccess)
                    } else {
                        showToast(R.string.time_range_settings_conflict)
                    }
                    update()
                }
            } else {
                showToast(R.string.emptyInput)
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private suspend fun update() {
        val timeRanges = withContext(Dispatchers.IO) {
            service.getAllTimeRanges()
        }
        
        withContext(Dispatchers.Main) {
            binding.trrtablelayout.adapter = TimeRangeRecyclerAdapter(timeRanges) { timeRange ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        service.deleteTimeRange(timeRange)
                    }
                    update()
                }
            }
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