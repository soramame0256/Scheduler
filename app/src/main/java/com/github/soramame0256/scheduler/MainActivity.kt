package com.github.soramame0256.scheduler

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.soramame0256.scheduler.ui.schedule.ScheduleSettingsActivity
import com.github.soramame0256.scheduler.ui.theme.SchedulerTheme
import com.github.soramame0256.scheduler.ui.timerange.TimeRangeSettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SchedulerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = {
                        val context = LocalContext.current

                        Column(modifier = Modifier.padding(it)) {
                            Button(onClick = {
                                context.startActivity(Intent(context, TimeRangeSettingsActivity::class.java))
                            }, modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Time Range Settings")
                            }
                            Button(onClick = {
                                context.startActivity(Intent(context, ScheduleSettingsActivity::class.java))
                            }, modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Schedule Settings")
                            }
                        }
                    }
                )
            }
        }

    }
}