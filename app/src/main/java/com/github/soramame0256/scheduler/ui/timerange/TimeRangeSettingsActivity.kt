package com.github.soramame0256.scheduler.ui.timerange

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.soramame0256.scheduler.R
import com.github.soramame0256.scheduler.databinding.TimeRangeSettingsActivityBinding
import com.github.soramame0256.scheduler.model.Time
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/*
後でJetpack Composeに書き換え
 */
@AndroidEntryPoint
class TimeRangeSettingsActivity : AppCompatActivity() {
    private lateinit var binding: TimeRangeSettingsActivityBinding
    private lateinit var timeRangeAdapter: TimeRangeRecyclerAdapter

    private val viewModel: TimeRangeSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TimeRangeSettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // RecyclerAdapterの初期化
        timeRangeAdapter = TimeRangeRecyclerAdapter { timeRange ->
            lifecycleScope.launch {
                viewModel.delete(timeRange)
            }
        }
        binding.trrtablelayout.adapter = timeRangeAdapter

        val editTextTimeStart = binding.editTextTimeStart
        val editTextTimeEnd = binding.editTextTimeEnd
        val addButton = binding.button
        viewModel.load()
        // UiEventの処理
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.timeRanges.collect { list ->
                        timeRangeAdapter.submitList(list)
                    }
                }
                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            is UiEvent.Message -> {
                                val resId = when (event.id) {
                                    MessageId.InsertSuccess -> R.string.time_range_insert_success
                                    MessageId.Conflict -> R.string.time_range_settings_conflict
                                    MessageId.StartAfterEnd -> R.string.start_time_later_than_end
                                }
                                Toast.makeText(this@TimeRangeSettingsActivity, resId, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
        // 追加処理
        addButton.setOnClickListener {
            val parsed = parseAndValidateInput(editTextTimeStart.text.toString(), editTextTimeEnd.text.toString())
            when (parsed) {
                is TimeInputResult.Empty -> showToast(R.string.empty_input)
                is TimeInputResult.InvalidFormat -> showToast(R.string.invalid_input)
                is TimeInputResult.Success -> {
                    val (start, end) = parsed
                    lifecycleScope.launch {
                        viewModel.add(start, end)
                    }
                }
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    private fun parseAndValidateInput(startTimeString: String, endTimeString: String): TimeInputResult {
        if (startTimeString.isEmpty() || endTimeString.isEmpty()) return TimeInputResult.Empty

        if (!TIME_VALIDATOR_REGEX.matches(startTimeString) ||
            !TIME_VALIDATOR_REGEX.matches(endTimeString)
        ) return TimeInputResult.InvalidFormat
        val start = parseTime(startTimeString)
        val end = parseTime(endTimeString)

        return TimeInputResult.Success(start, end)
    }

    private fun parseTime(timeString: String): Time {
        val parts = timeString.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        return Time(hour, minute)
    }

    sealed class TimeInputResult {
        data class Success(val start: Time, val end: Time) : TimeInputResult()
        object Empty : TimeInputResult()
        object InvalidFormat : TimeInputResult()
    }

    companion object {
        private val TIME_VALIDATOR_REGEX = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$".toRegex()
    }
}