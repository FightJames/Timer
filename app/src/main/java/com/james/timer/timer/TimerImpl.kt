package com.james.timer.timer

import android.os.CountDownTimer
import android.os.SystemClock
import cancelChildren
import com.james.timer.model.TimerData
import com.james.timer.model.TimerState
import com.james.timer.repository.DBRepository
import com.james.timer.utils.JobManagerImpl
import com.james.timer.utils.SoundManager
import io
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import serialJobManager
import timber.log.Timber

class TimerImpl : Timer {

    override val timerData: TimerData
        get() = _timerData.copy()

    private val _timerData: TimerData
    private val _currentTimeFlow: MutableStateFlow<Long>
    private var currentTime: Long
    private var timerState: TimerState
    private val dbRepository: DBRepository
    private val soundManager: SoundManager

    constructor(timerData: TimerData, dbRepository: DBRepository, soundManager: SoundManager) {
        this.soundManager = soundManager
        this._timerData = timerData
        this.dbRepository = dbRepository
        _currentTimeFlow = MutableStateFlow(timerData.currentCountDown)
        currentTime = timerData.currentCountDown
        timerState = timerData.state
        if (timerState == TimerState.RUNNING) {
            start()
        }
    }

    override val currentTimeFlow: StateFlow<Long>
        get() = _currentTimeFlow

    override fun start() {
        timerState = TimerState.RUNNING
        _timerData.state = TimerState.RUNNING
        syncTimerData()
        startInternalWithCountDownTimer()
    }

    override fun resume() {
        timerState = TimerState.RUNNING
        _timerData.state = TimerState.RUNNING
        syncTimerData()
        startInternalWithCountDownTimer()

    }

    @Synchronized
    override fun pause() {
        stopInternalCountDownTimer()
        timerState = TimerState.PAUSE
        _timerData.state = TimerState.PAUSE
        soundManager.stopRinging(_timerData.createTime.toString())
        syncTimerData()
    }

    @Synchronized
    override fun stop() {
        stopInternalCountDownTimer()
        _currentTimeFlow.compareAndSet(_currentTimeFlow.value, _timerData.countDownTime)
        currentTime = _timerData.countDownTime
        _timerData.currentCountDown = _timerData.countDownTime
        timerState = TimerState.STOP
        _timerData.state = TimerState.STOP
        soundManager.stopRinging(_timerData.createTime.toString())
        syncTimerData()
    }

    private var countDownTimer: CountDownTimer? = null
    @Synchronized
    private fun startInternalWithCountDownTimer() {
        if (countDownTimer != null) return
        countDownTimer = object : CountDownTimer(currentTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentTime = millisUntilFinished
                _timerData.currentCountDown = currentTime
                _currentTimeFlow.update { currentTime }
                syncTimerData()
            }

            override fun onFinish() {
                currentTime = 0
                _timerData.currentCountDown = currentTime
                soundManager.startRinging(_timerData.createTime.toString())
                _currentTimeFlow.update { currentTime }
                syncTimerData()
            }
        }
        countDownTimer?.start()
    }

    @Synchronized
    private fun stopInternalCountDownTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    private fun syncTimerData() {
        serialJobManager.launchSafely {
            dbRepository.updateTimerData(_timerData)
        }
    }
}