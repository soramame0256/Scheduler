package com.github.soramame0256.scheduler.ui.timerange

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.github.soramame0256.scheduler.databinding.TimeRangeRecyclerListBinding
import com.github.soramame0256.scheduler.model.TimeRange

class TimeRangeRecyclerAdapter(private val onClick: (TimeRange) -> Unit) : ListAdapter<TimeRange, TimeRangeViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeRangeViewHolder {
        val binding = TimeRangeRecyclerListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeRangeViewHolder(binding)
    }
    override fun onBindViewHolder(holder: TimeRangeViewHolder, position: Int) {
        val timeRange = getItem(position)
        holder.bind(timeRange, onClick)
    }


    companion object DiffCallback : DiffUtil.ItemCallback<TimeRange>() {
        override fun areItemsTheSame(oldItem: TimeRange, newItem: TimeRange): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TimeRange, newItem: TimeRange): Boolean {
            return oldItem == newItem
        }
    }
}