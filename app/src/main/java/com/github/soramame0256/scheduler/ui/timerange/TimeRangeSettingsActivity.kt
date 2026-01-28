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
import androidx.compose.ui.text.style.TextAlign
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
    var pickerDialogTarget by remember { mutableStateOf(PickerDialogTarget.NONE) }
    var endTime: Time by remember { mutableStateOf(Time(0, 0)) }
    var startTime: Time by remember { mutableStateOf(Time(0, 0)) }

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
                onInteract = { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                pickerDialogTarget = PickerDialogTarget.START
                            }
                        }
                    }
                },
                label = { Text(text = stringResource(R.string.time_range_settings_starttime_in)) },
                shownValue = startTime.toString()
            )
            InteractiveTimePickerBox(
                modifier = Modifier.weight(weight = 1f),
                onInteract = { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                pickerDialogTarget = PickerDialogTarget.END
                            }
                        }
                    }
                },
                label = { Text(text = stringResource(R.string.time_range_settings_endtime_in)) },
                shownValue = endTime.toString()
            )
            if (pickerDialogTarget != NONE) {
                val activeTime = when (pickerDialogTarget) {
                    START -> startTime
                    END -> endTime
                    else -> error("Invalid pickerDialogTarget!") // 起こりえない
                }
                val timePickerState = rememberTimePickerState(
                    initialHour = activeTime.hour,
                    initialMinute = activeTime.minute,
                    is24Hour = true
                )

                TimePickerDialog(
                    onDismissRequest = { pickerDialogTarget = NONE },
                    confirmButton = {
                        TextButton(onClick = {
                            when (pickerDialogTarget) {
                                START -> startTime = Time(timePickerState.hour, timePickerState.minute)
                                END -> endTime = Time(timePickerState.hour, timePickerState.minute)
                                else -> error("Invalid pickerDialogTarget!")
                            }
                            pickerDialogTarget = NONE
                        }) {
                            Text(text = stringResource(R.string.ok))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { pickerDialogTarget = NONE }) {
                            Text(text = stringResource(R.string.cancel))
                        }
                    }
                ) {
                    TimePicker(state = timePickerState)
                }
            }
        }
        Button(
            onClick = {
                pickerDialogTarget = NONE
                onAdd(startTime, endTime)
                // 入力フィールドをクリア
                startTime = Time(0, 0)
                endTime = Time(0, 0)
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
private fun InteractiveTimePickerBox(
    modifier: Modifier = Modifier,
    onInteract: @Composable (MutableInteractionSource) -> Unit,
    label: @Composable () -> Unit,
    shownValue: String
){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        OutlinedTextField(
            value = shownValue,
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
