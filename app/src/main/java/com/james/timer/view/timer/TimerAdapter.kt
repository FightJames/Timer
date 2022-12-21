package com.james.timer.view.timer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.james.timer.R
import com.james.timer.model.TimerData
import kotlinx.coroutines.CoroutineScope

class TimerAdapter(
    private var list: List<TimerData>,
    private val viewModel: TimerViewModel,
) : RecyclerView.Adapter<TimerViewHolder>() {

    lateinit  var viewLifeCycleScope: CoroutineScope
    fun updateData(list: List<TimerData>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.timer_item, parent, false)
        return TimerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.onBind(list[position], viewModel, viewLifeCycleScope)
    }


    override fun getItemCount(): Int = list.size
}