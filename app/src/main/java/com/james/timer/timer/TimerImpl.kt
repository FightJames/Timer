package com.james.timer.timer

import cancelChildren
import com.james.timer.model.TimerData
import com.james.timer.model.TimerState
import com.james.timer.utils.JobManagerImpl
import io
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class TimerImpl : Timer {

    override val timerData: TimerData
        get() = _timerData.copy()

    private val _timerData: TimerData
    private val _currentTimeFlow: MutableStateFlow<Long>
    private var currentTime: Long
    private val coroutineScope = JobManagerImpl(SupervisorJob() + io())
    private var timerJob: Job = CompletableDeferred(Unit)
    private var timerState: TimerState

    constructor(timerData: TimerData) {
        this._timerData = timerData
        _currentTimeFlow = MutableStateFlow(timerData.currentCountDown)
        currentTime = timerData.currentCountDown
        timerState = timerData.state
        if (timerState == TimerState.RUNNING) {
            start()
        }
    }

    override val currentTimeFlow: StateFlow<Long>
        get() = _currentTimeFlow

    @Volatile
    private var flag = false
    private val delayMiliSecond = 1000L

    override fun start() {
        timerState = TimerState.RUNNING
        startInternal()
    }

    override fun resume() {
        timerState = TimerState.RUNNING
        startInternal()
    }

    @Synchronized
    override fun pause() {
        flag = false
        coroutineScope.cancelChildren()
        timerState = TimerState.PAUSE
    }

    @Synchronized
    override fun stop() {
        flag = false
        coroutineScope.cancelChildren()
        _currentTimeFlow.compareAndSet(_currentTimeFlow.value, _timerData.countDownTime)
        timerState = TimerState.STOP
    }

    @Synchronized
    private fun startInternal() {
        if (timerJob.isActive) return
        timerJob = coroutineScope.launchSafely {
            flag = true
            while (flag) {
                delay(delayMiliSecond)
                currentTime -= 1000
                _timerData.currentCountDown = currentTime
                _currentTimeFlow.compareAndSet(_currentTimeFlow.value, currentTime)
            }
        }
    }
}