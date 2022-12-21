package com.james.timer.timer

import com.james.timer.model.TimerData
import kotlinx.coroutines.flow.StateFlow


interface Timer {

    val currentTimeFlow: StateFlow<Long>

    val timerData: TimerData

    fun start()

    fun resume()

    fun pause()

    fun stop()
}
