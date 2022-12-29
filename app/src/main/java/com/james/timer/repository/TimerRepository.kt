package com.james.timer.repository

import com.james.timer.model.TimerData
import com.james.timer.timer.TimerManager
import io
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TimerRepository @Inject constructor(
    private val timerManager: TimerManager
) {

    suspend fun getAllTimerData(): List<TimerData> = timerManager.getTimerDataList()

    suspend fun addTimerData(timerData: TimerData) = withContext(io()) {
        timerManager.addTimerData(timerData)
    }

    suspend fun start(createTime: Long) {
        timerManager.getTimer(createTime).start()
    }

    suspend fun resume(createTime: Long) {
        timerManager.getTimer(createTime).resume()
    }

    suspend fun pause(createTime: Long) {
        timerManager.getTimer(createTime).pause()
    }

    suspend fun stop(createTime: Long) {
        timerManager.getTimer(createTime).stop()
    }

    suspend fun removeTimer(timerData: TimerData) {
        timerManager.deleteTimerData(timerData)
    }

    suspend fun getTimerCurrentTimeFlow(
        createTime: Long
    ): StateFlow<Long> = withContext(io()) {
        timerManager.getTimer(createTime).currentTimeFlow
    }

    suspend fun getTimerCurrentTime(createTime: Long): Long =
        timerManager.getTimer(createTime).currentTimeFlow?.value ?: -1L
}