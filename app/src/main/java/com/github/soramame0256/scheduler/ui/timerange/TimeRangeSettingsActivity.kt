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
            val validationResult = validate(editTextTimeStart.text.toString(), editTextTimeEnd.text.toString())
            when (validationResult) {
                ValidationResult.EMPTY -> {
                    showToast(R.string.empty_input)
                }
                ValidationResult.INVALID -> {
                    showToast(R.string.invalid_input)
                }
                else -> {
                    val (start, end) = castInput(editTextTimeStart.text.toString(), editTextTimeEnd.text.toString()).getOrNull() ?: run {
                        showToast(R.string.invalid_input)
                        return@setOnClickListener
                    }
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

    private fun parseTimeParts(timeParts: List<String>): Pair<Int, Int>? {
        if (timeParts.size != 2) return null

        val hour = timeParts[0].toIntOrNull() ?: return null
        val minute = timeParts[1].toIntOrNull() ?: return null

        return Pair(hour, minute)
    }

    private fun validate(startTimeString: String, endTimeString: String) : ValidationResult {
        return if (startTimeString.isNotEmpty() && endTimeString.isNotEmpty()) {
            if (timeValidator.matcher(startTimeString).matches() && timeValidator.matcher(endTimeString).matches()) {
                val (start, end) = castInput(startTimeString, endTimeString).getOrNull() ?: return ValidationResult.INVALID
                if (start >= end) return ValidationResult.INVALID
                ValidationResult.VALID
            } else {
                ValidationResult.INVALID
            }
        } else {
            ValidationResult.EMPTY
        }
    }
    private fun castInput(startTimeString: String, endTimeString: String): Result<Pair<Time, Time>> {
        val startTimeParts = startTimeString.split(":")
        val endTimeParts = endTimeString.split(":")
        val (startHour, startMinute) = parseTimeParts(startTimeParts) ?: run {
            return Result.failure(IllegalArgumentException("Invalid time format"))
        }

        val (endHour, endMinute) = parseTimeParts(endTimeParts) ?: run {
            return Result.failure(IllegalArgumentException("Invalid time format"))
        }
        return Result.success(Time(startHour, startMinute) to Time(endHour, endMinute))
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
    enum class ValidationResult {
        VALID, INVALID, EMPTY
    }
    enum class InsertionResult {
        SUCCESS, CONFLICT
    }
    companion object {
        @JvmStatic
        val timeValidator: Pattern = Pattern.compile("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
    }
}