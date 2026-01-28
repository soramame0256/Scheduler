package com.github.soramame0256.scheduler.ui.schedule

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.github.soramame0256.scheduler.R
import com.github.soramame0256.scheduler.ui.theme.SchedulerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleSettingsActivity : ComponentActivity() {
    private val viewModel: ScheduleSettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.load()
        setContent {
            SchedulerTheme {
                ScheduleSettingsScreen(viewModel)
            }
        }
    }
}

@Composable
private fun ScheduleSettingsScreen(viewModel: ScheduleSettingsViewModel) {
    val context = LocalContext.current
    val schedules by viewModel.schedules.collectAsState()
    val events by viewModel.events.collectAsState()
    // イベントのハンドリング
    LaunchedEffect(events) {
        events.handle { event ->
            if (event is ScheduleUiEvent.NoOperation) return@handle

            if (event is ScheduleUiEvent.Message) {
                val resId = when (event.id) {
                    else -> R.string.no_schedule
                }
                Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
            }
        }
    }

}