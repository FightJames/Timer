package com.james.timer.view.timer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.james.timer.R
import com.james.timer.model.TimerData
import kotlinx.coroutines.CoroutineScope

class TimerAdapter(
    var list: List<TimerData>,
    private val viewModel: TimerViewModel,
) : RecyclerView.Adapter<TimerViewHolder>() {

    private var modifyPostion = -1
    lateinit var viewLifeCycleScope: CoroutineScope
    fun updateData(list: List<TimerData>, scrollToTop: () -> Unit) {
        if (modifyPostion == -1) {
            this.list = list
            notifyDataSetChanged()
        } else {
            dataChange(list, scrollToTop)
        }
    }

    private fun dataChange(newList: List<TimerData>, scrollToTop: () -> Unit) {
        val oldList = this.list
        this.list = newList
        when {
            oldList.size == newList.size -> {
                val (newIdx, timerData) = kotlin.run {
                    newList.forEachIndexed { index, timerData ->
                        if (timerData.createTime == oldList[modifyPostion].createTime) {
                            return@run Pair(index, timerData)
                        }
                    }
                    return@run Pair(-1, null)
                }
                if (newIdx == modifyPostion) {
                    notifyItemChanged(modifyPostion)
                } else {
                    notifyItemMoved(modifyPostion, newIdx)
                    val start = Math.min(modifyPostion, newIdx)
                    val count = Math.max(modifyPostion, newIdx) - start + 1
                    notifyItemRangeChanged(start, count)
                    if (newIdx == 0) {
                        scrollToTop()
                    }
                }
            }
            else -> notifyDataSetChanged()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.timer_item, parent, false)
        return TimerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.onBind(list[position], viewModel, viewLifeCycleScope) {
            modifyPostion = it
        }
    }


    override fun getItemCount(): Int = list.size
}