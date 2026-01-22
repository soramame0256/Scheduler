package com.github.soramame0256.scheduler.ui.timerange

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.github.soramame0256.scheduler.R
import com.github.soramame0256.scheduler.model.TimeRange

class TimeRangeRecyclerAdapter(val onClick: (TimeRange) -> Unit) : ListAdapter<TimeRange, TimeRangeViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeRangeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.time_range_recycler_list, parent, false)
        return TimeRangeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TimeRangeViewHolder, position: Int) {
        val timeRange = getItem(position)
        holder.range.text = timeRange.toString()
        holder.deleteButton.setOnClickListener { onClick(timeRange) }
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