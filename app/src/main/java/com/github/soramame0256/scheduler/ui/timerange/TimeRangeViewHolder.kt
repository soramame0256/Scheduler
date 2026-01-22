package com.github.soramame0256.scheduler.ui.timerange

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.soramame0256.scheduler.databinding.TimeRangeRecyclerListBinding

class TimeRangeViewHolder(item: View) : RecyclerView.ViewHolder(item){
    val binding = TimeRangeRecyclerListBinding.bind(item)
    val range = binding.timeRange
    val deleteButton = binding.deleteButton
}