package com.james.timer.repository

import com.james.timer.model.TimerData
import com.james.timer.service.DBService
import com.james.timer.timer.TimerManager
import dagger.hilt.android.scopes.ViewModelScoped
import io
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class TimerRepository @Inject constructor(
    private val service: DBService,
    private val timerManager: TimerManager
) {

    suspend fun addTimerData(timerData: TimerData) = withContext(io()) {
        service.saveTimerData(timerData)
        timerManager.addTimerData(timerData)
        Timber.d("all timer ${service.getAllTimersData().toString()}")
    }

    suspend fun getAllTimerData(): List<TimerData> = timerManager.timerDataList

    suspend fun start(createTime: Long) {
        timerManager.getTimer(createTime)?.start()
    }

    suspend fun resume(createTime: Long) {
        timerManager.getTimer(createTime)?.resume()
    }

    suspend fun pause(createTime: Long) {
        timerManager.getTimer(createTime)?.pause()
    }

    suspend fun stop(createTime: Long) {
        timerManager.getTimer(createTime)?.stop()
    }

    suspend fun subscribeTimerCurrentTime(createTime: Long, collector: FlowCollector<Long>) =
        withContext(io()) {
            timerManager.getTimer(createTime)?.currentTimeFlow?.collect {
                timerManager.getTimerData(createTime)?.let { timerData -> service.updateTimerData(timerData) }
                collector.emit(it)
            }
        }

    suspend fun getTimerCurrentTime(createTime: Long): Long =
        timerManager.getTimer(createTime)?.currentTimeFlow?.value ?: -1L
}