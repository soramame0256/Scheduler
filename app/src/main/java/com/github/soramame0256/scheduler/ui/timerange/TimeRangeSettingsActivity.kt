package com.github.soramame0256.scheduler.ui.timerange

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.soramame0256.scheduler.R
import com.github.soramame0256.scheduler.model.Time
import com.github.soramame0256.scheduler.model.TimeRange
import com.github.soramame0256.scheduler.ui.theme.SchedulerTheme
import com.github.soramame0256.scheduler.ui.timerange.PickerDialogTarget.*
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
    var startTime by remember { mutableStateOf(Time(0, 0)) }
    var endTime by remember { mutableStateOf(Time(0, 0)) }
    // イベントのハンドリング
    LaunchedEffect(events) {
        events.handle { event ->
            if (event is UiEvent.NoOperation) return@handle

            if (event is UiEvent.Message) {
                val resId = when (event.id) {
                    MessageId.InsertSuccess -> {
                        // 入力値のリセット
                        startTime = Time(0, 0)
                        endTime = Time(0, 0)

                        R.string.time_range_insert_success
                    }

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
            TimeRangeInput(
                startTime = startTime,
                endTime = endTime,
                onStartTimeChange = { startTime = it },
                onEndTimeChange = { endTime = it },
                onAdd = { start, end -> viewModel.add(start, end) }
            )
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
private fun TimeRangeInput(
    startTime: Time,
    endTime: Time,
    onStartTimeChange: (Time) -> Unit,
    onEndTimeChange: (Time) -> Unit,
    onAdd: (Time, Time) -> Unit,
) {
    var pickerDialogTarget by remember { mutableStateOf(NONE) }
    Column {
        Text(
            text = stringResource(id = R.string.time_range_settings_category_add),
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            InteractiveTimePickerBox(
                modifier = Modifier.weight(weight = 1f),
                onClick = {
                    pickerDialogTarget = START
                },
                label = { Text(text = stringResource(R.string.time_range_settings_starttime_in)) },
                shownValue = startTime.toString()
            )
            InteractiveTimePickerBox(
                modifier = Modifier.weight(weight = 1f),
                onClick = {
                    pickerDialogTarget = END
                },
                label = { Text(text = stringResource(R.string.time_range_settings_endtime_in)) },
                shownValue = endTime.toString()
            )
            if (pickerDialogTarget != NONE) {
                TimePickerDialogWrapper(
                    activeTime = when (pickerDialogTarget) {
                        START -> startTime
                        END -> endTime
                        else -> error("Invalid pickerDialogTarget!") // 起こりえない
                    },
                    onDismiss = { pickerDialogTarget = NONE },
                    onConfirm = { time ->
                        when (pickerDialogTarget) {
                            START -> onStartTimeChange(time)
                            END -> onEndTimeChange(time)
                            else -> error("Invalid pickerDialogTarget!")
                        }
                        pickerDialogTarget = NONE
                    }
                )
            }
        }
        Button(
            onClick = {
                pickerDialogTarget = NONE
                onAdd(startTime, endTime)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(stringResource(id = R.string.add))
        }
    }
}

private enum class PickerDialogTarget {
    START, END, NONE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialogWrapper(
    activeTime: Time,
    onDismiss: () -> Unit,
    onConfirm: (Time) -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = activeTime.hour,
        initialMinute = activeTime.minute,
        is24Hour = true
    )
    TimePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                val time = Time(timePickerState.hour, timePickerState.minute)
                onConfirm(time)
            }) {
                Text(text = stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    ) {
        TimePicker(state = timePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InteractiveTimePickerBox(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    shownValue: String,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect {
                if (it is PressInteraction.Release) {
                    onClick()
                }
            }
        }
        OutlinedTextField(
            value = shownValue,
            onValueChange = { },
            readOnly = true,
            label = label,
            interactionSource = interactionSource
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
            modifier = Modifier.weight(weight = 1f)
        )
        Button(onClick = onDelete) {
            Text(text = stringResource(id = R.string.header_delete))
        }
    }
}
