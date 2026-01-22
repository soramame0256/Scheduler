package com.github.soramame0256.scheduler.ui.timerange

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.soramame0256.scheduler.R
import com.github.soramame0256.scheduler.model.TimeRange

class TimeRangeRecyclerAdapter(val list: List<TimeRange>, val onClick: (TimeRange) -> Unit) : RecyclerView.Adapter<TimeRangeViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeRangeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.time_range_recycler_list, parent, false)
        return TimeRangeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TimeRangeViewHolder, position: Int) {
        holder.range.text = list[position].toString()
        holder.deleteButton.setOnClickListener { onClick(list[position]) }
    }

    override fun getItemCount() = list.size

}