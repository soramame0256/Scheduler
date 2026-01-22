package com.github.soramame0256.scheduler.ui.timerange

import androidx.recyclerview.widget.RecyclerView
import com.github.soramame0256.scheduler.databinding.TimeRangeRecyclerListBinding
import com.github.soramame0256.scheduler.model.TimeRange

class TimeRangeViewHolder(private val binding: TimeRangeRecyclerListBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(timeRange: TimeRange, onClick: (TimeRange) -> Unit) {
        binding.timeRange.text = timeRange.toString()
        binding.deleteButton.setOnClickListener { onClick(timeRange) }
    }
}