package com.james.timer.view.timer

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import clickWithDebounce
import com.james.timer.R
import com.james.timer.model.TimerData
import com.james.timer.model.TimerState
import com.james.timer.utils.rethrowOnCancellation
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import milliSecondToTimeString
import timber.log.Timber

class TimerViewHolder(view: View) : ViewHolder(view) {
    private var currentTimeJob: Job = CompletableDeferred(Unit)

    fun onBind(
        timerData: TimerData,
        viewModel: TimerViewModel,
        viewLifeCycleOwner: CoroutineScope
    ) {
        val timerStr = itemView.context.resources.getString(R.string.timer)
        itemView.findViewById<TextView>(R.id.timerTitleText).text = "${milliSecondToTimeString(timerData.countDownTime)} $timerStr"
        val resetBtn = itemView.findViewById<ImageView>(R.id.resetBtn)
        resetBtn.visibility = if (timerData.state == TimerState.STOP) {
            View.GONE
        } else {
            View.VISIBLE
        }
        resetBtn.clickWithDebounce {
            viewModel.stop(timerData.createTime)
        }
        val startOrStopBtn = itemView.findViewById<ImageView>(R.id.startOrStopBtn)
        startOrStopBtn.setImageResource(
            if (timerData.state == TimerState.RUNNING) {
                R.drawable.ic_baseline_retengle_pause_24
            } else {
                R.drawable.ic_baseline_retengle_play_24
            }
        )
        startOrStopBtn.clickWithDebounce {
            if (timerData.state == TimerState.RUNNING) {
                viewModel.pause(timerData.createTime)
            } else {
                viewModel.start(timerData.createTime)
            }
        }

        itemView.findViewById<ImageView>(R.id.closeBtn).clickWithDebounce {
            viewModel.removeTimer(timerData)
        }

        val timeText = itemView.findViewById<TextView>(R.id.timeText)
        currentTimeJob.cancel()
        currentTimeJob = viewLifeCycleOwner.launch {
            try {
                timeText.text =
                    milliSecondToTimeString(viewModel.getTimerCurrentTime(timerData.createTime))
                viewModel.getTimerCurrentTimeFlow(timerData.createTime).collect {
                    withContext(Dispatchers.Main.immediate) {
                        timeText.text = milliSecondToTimeString(it)
                    }
                }
            } catch (t: Throwable) {
                t.rethrowOnCancellation()
                Timber.e("Exception $t")
            }
        }
    }
}