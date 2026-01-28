package com.github.soramame0256.scheduler.ui.timerange

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.soramame0256.scheduler.R
import com.github.soramame0256.scheduler.model.Time
import com.github.soramame0256.scheduler.model.TimeRange
import com.github.soramame0256.scheduler.ui.theme.SchedulerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimeRangeSettingsActivity : ComponentActivity() {

    private val viewModel: TimeRangeSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.load()
        setContent {
            SchedulerTheme {
                TimeRangeSettingsScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeRangeSettingsScreen(viewModel: TimeRangeSettingsViewModel) {

    val context = LocalContext.current
    val timeRanges by viewModel.timeRanges.collectAsState()
    val events by viewModel.events.collectAsState()

    // イベントのハンドリング
    LaunchedEffect(events) {
        events.handle { event ->
            if (event is UiEvent.NoOperation) return@handle

            if (event is UiEvent.Message) {
                val resId = when (event.id) {
                    MessageId.InsertSuccess -> R.string.time_range_insert_success
                    MessageId.Conflict -> R.string.time_range_settings_conflict
                    MessageId.StartAfterEnd -> R.string.start_time_later_than_end
                }
                Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.title_activity_time_range_settings)) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            TimeRangeInput(onAdd = { start, end -> viewModel.add(start, end) })
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Text(
                text = stringResource(id = R.string.time_range_settings_category_remove),
                style = MaterialTheme.typography.titleLarge
            )
            LazyColumn(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(timeRanges, key = { it.id }) { timeRange ->
                    TimeRangeItem(
                        timeRange = timeRange,
                        onDelete = { viewModel.delete(timeRange) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeRangeInput(onAdd: (Time, Time) -> Unit) {
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState(is24Hour = true)
    val endTimePickerState = rememberTimePickerState(is24Hour = true)
    Column {
        Text(
            text = stringResource(id = R.string.time_range_settings_category_add),
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InteractiveTimePickerBox(
                onInteract = { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                showStartTimePicker = true
                                showEndTimePicker = false
                            }
                        }
                    }
                },
                label = { Text(text = "開始時間を選択") },
                pickerState = startTimePickerState
            )
            InteractiveTimePickerBox(
                onInteract = { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                showStartTimePicker = false
                                showEndTimePicker = true
                            }
                        }
                    }
                },
                label = { Text(text = "終了時間を選択") },
                pickerState = endTimePickerState
            )
            if (showStartTimePicker) {
                TimePickerDialog(
                    onDismissRequest = { showStartTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = { showStartTimePicker = false }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showStartTimePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    TimePicker(state = startTimePickerState)
                }
            } else if (showEndTimePicker) {
                TimePickerDialog(
                    onDismissRequest = { showEndTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = { showEndTimePicker = false }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEndTimePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    TimePicker(state = endTimePickerState)
                }
            }
        }
        Button(
            onClick = {
                showStartTimePicker = false
                showEndTimePicker = false
                val start = Time(startTimePickerState.hour, startTimePickerState.minute)
                val end = Time(endTimePickerState.hour, endTimePickerState.minute)
                onAdd(start, end)
                // 入力フィールドをクリア
                startTimePickerState.hour = 0
                startTimePickerState.minute = 0
                endTimePickerState.hour = 0
                endTimePickerState.minute = 0
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(stringResource(id = R.string.add))
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InteractiveTimePickerBox(
    onInteract: @Composable (MutableInteractionSource) -> Unit,
    label: @Composable () -> Unit,
    pickerState: TimePickerState
){
    Box(
        contentAlignment = Alignment.Center
    ) {
        OutlinedTextField(
            value = "${pickerState.hour}:${pickerState.minute}",
            onValueChange = { },
            readOnly = true,
            label = label,
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    onInteract(interactionSource)
                }
        )
    }
}
@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = content
    )
}
@Composable
private fun TimeRangeItem(timeRange: TimeRange, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = timeRange.toString(),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Button(onClick = onDelete) {
            Text(text = stringResource(id = R.string.header_delete))
        }
    }
}