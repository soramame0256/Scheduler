package com.github.soramame0256.scheduler.ui.schedule

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import com.github.soramame0256.scheduler.model.Schedule
import com.github.soramame0256.scheduler.model.TimeRange
import com.github.soramame0256.scheduler.model.Weekday
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleSettingsScreen(viewModel: ScheduleSettingsViewModel) {
    val context = LocalContext.current
    val schedules by viewModel.schedules.collectAsState()
    val events by viewModel.events.collectAsState()
    val timeRanges by viewModel.timeRanges.collectAsState()

    val weekday by viewModel.selectedWeekday.collectAsState()
    val timeRange by viewModel.selectedTimeRange.collectAsState()
    val message by viewModel.message.collectAsState()
    // イベントのハンドリング
    LaunchedEffect(events) {
        events.handle { event ->
            if (event is ScheduleUiEvent.NoOperation) return@handle

            if (event is ScheduleUiEvent.Message) {
                val resId = when (event.id) {
                    ScheduleInsertMessageId.Conflict -> R.string.settings_conflict
                    ScheduleInsertMessageId.InsertSuccess -> R.string.insert_success
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
            ScheduleInput(
                onAdd = {
                    viewModel.add(weekday, timeRange, message)
                },
                timeRanges = timeRanges,
                onWeekdayUpdate = { viewModel.updateSelectedWeekday(it) },
                onTimeRangeUpdate = { viewModel.updateSelectedTimeRange(it) },
                onMessageUpdate = { viewModel.updateMessage(it) },
                weekday = weekday,
                timeRange = timeRange,
                message = message
            )
            LazyColumn(
                modifier = Modifier.padding(top = 8.dp).weight(1f)
            ) {
                items(schedules, key = { "${it.weekday.value} ${it.timeRange.id}" }) { schedule ->
                    ScheduleItem(
                        schedule = schedule,
                        onDelete = { viewModel.delete(schedule) }
                    )
                }
            }
        }

    }
}

@Composable
private fun ScheduleInput(
    onAdd: () -> Unit,
    timeRanges: List<TimeRange>,
    onWeekdayUpdate: (Weekday) -> Unit = {},
    onTimeRangeUpdate: (TimeRange) -> Unit = {},
    onMessageUpdate: (String) -> Unit = {},
    weekday: Weekday,
    timeRange: TimeRange,
    message: String
) {

    Column {
        Text(
            text = stringResource(R.string.time_range_settings_category_add),
            style = MaterialTheme.typography.titleLarge
        )
        WeekdayInput(
            onSelectedChange = onWeekdayUpdate,
            selected = weekday
        )
        TimeRangeInput(
            onSelectedChange = onTimeRangeUpdate,
            timeRanges = timeRanges,
            selected = timeRange
        )
        OutlinedTextField(
            value = message,
            onValueChange = onMessageUpdate,
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { onAdd() }) {
            Text(text = "Add")
        }
    }
}

@Composable
private fun WeekdayInput(
    modifier: Modifier = Modifier,
    onSelectedChange: (Weekday) -> Unit,
    selected: Weekday
) {
    WeekdayDropdown(
        modifier = modifier,
        selected = selected,
        onSelectedChange = { onSelectedChange(it) },
        label = "Weekday"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeekdayDropdown(
    modifier: Modifier = Modifier,
    options: List<Weekday> = Weekday.entries.filterNot { weekday -> weekday == Weekday.ERROR },
    selected: Weekday,
    onSelectedChange: (Weekday) -> Unit,
    label: String = "Weekday"
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected.name,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TimeRangeInput(
    modifier: Modifier = Modifier,
    timeRanges: List<TimeRange>,
    onSelectedChange: (TimeRange) -> Unit,
    selected: TimeRange
) {
    TimeRangeDropdown(
        modifier = modifier,
        selected = selected,
        onSelectedChange = { onSelectedChange(it) },
        label = "TimeRange",
        options = timeRanges
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeRangeDropdown(
    modifier: Modifier = Modifier,
    options: List<TimeRange>,
    selected: TimeRange,
    onSelectedChange: (TimeRange) -> Unit,
    label: String = "TimeRange"
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected.toString(),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString(), style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
@Composable
private fun ScheduleItem(schedule: Schedule, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${schedule.weekday.name} ${schedule.timeRange}: ${schedule.message}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(weight = 1f)
        )
        Button(onClick = onDelete) {
            Text(text = stringResource(id = R.string.header_delete))
        }
    }
}
