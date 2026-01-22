package com.github.soramame0256.scheduler.ui.timerange

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.soramame0256.scheduler.R
import com.github.soramame0256.scheduler.databinding.TimeRangeSettingsActivityBinding
import com.github.soramame0256.scheduler.model.Time
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
    private lateinit var timeRangeAdapter: TimeRangeRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TimeRangeSettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // RecyclerAdapterの初期化
        timeRangeAdapter = TimeRangeRecyclerAdapter { timeRange ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    service.deleteTimeRange(timeRange)
                }
                update()
            }
        }
        binding.trrtablelayout.adapter = timeRangeAdapter

        val editTextTimeStart = binding.editTextTimeStart
        val editTextTimeEnd = binding.editTextTimeEnd
        val addButton = binding.button
        lifecycleScope.launch {
            update()
        }
        addButton.setOnClickListener {
           val parsed = parseAndValidateInput(editTextTimeStart.text.toString(), editTextTimeEnd.text.toString())
            when (parsed) {
                is TimeInputResult.Empty -> showToast(R.string.empty_input)
                is TimeInputResult.InvalidFormat -> showToast(R.string.invalid_input)
                is TimeInputResult.StartAfterEnd -> showToast(R.string.start_time_later_than_end)
                is TimeInputResult.Success -> {
                    val (start, end) = parsed
                    lifecycleScope.launch {
                        val insertionResult = tryInsertTimeRange(start, end)
                        when (insertionResult) {
                            InsertionResult.SUCCESS -> showToast(R.string.time_range_insert_success)
                            InsertionResult.CONFLICT -> showToast(R.string.time_range_settings_conflict)
                        }
                        update()
                    }
                }
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private suspend fun update() {
        val timeRanges = withContext(Dispatchers.IO) {
            service.getAllTimeRanges()
        }

        withContext(Dispatchers.Main) {
            timeRangeAdapter.submitList(timeRanges)
        }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    private fun parseAndValidateInput(startTimeString: String, endTimeString: String): TimeInputResult {
        if (startTimeString.isEmpty() || endTimeString.isEmpty()) return TimeInputResult.Empty

        if (!timeValidator.matcher(startTimeString).matches() ||
            !timeValidator.matcher(endTimeString).matches()) return TimeInputResult.InvalidFormat

        val start = parseTime(startTimeString) ?: return TimeInputResult.InvalidFormat
        val end = parseTime(endTimeString) ?: return TimeInputResult.InvalidFormat

        if (start >= end) return TimeInputResult.StartAfterEnd

        return TimeInputResult.Success(start, end)
    }

    private fun parseTime(timeString: String): Time? {
        val parts = timeString.split(":")
        val hour = parts[0].toIntOrNull() ?: return null
        val minute = parts[1].toIntOrNull() ?: return null
        return Time(hour, minute)
    }
    private suspend fun tryInsertTimeRange(start: Time, end: Time) : InsertionResult {
        val conflicts = withContext(Dispatchers.IO) {
            service.countConflicts(start, end)
        }
        if (conflicts == 0) {
            withContext(Dispatchers.IO) {
                service.insertTimeRange(start, end)
            }
            return InsertionResult.SUCCESS
        } else {
            return InsertionResult.CONFLICT
        }
    }
    sealed class TimeInputResult {
        data class Success(val start: Time, val end: Time) : TimeInputResult()
        object Empty : TimeInputResult()
        object InvalidFormat : TimeInputResult()
        object StartAfterEnd : TimeInputResult() // 開始時間が終了時間より後
    }
    enum class InsertionResult {
        SUCCESS, CONFLICT
    }
    companion object {
        @JvmStatic
        val timeValidator: Pattern = Pattern.compile("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
    }
}