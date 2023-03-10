package com.james.timer.view.createTimer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.james.timer.model.Time
import com.james.timer.model.TimerData
import com.james.timer.model.TimerState
import com.james.timer.repository.TimerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import milliSecondToTimeString
import timber.log.Timber
import toMilliSecond
import javax.inject.Inject

@HiltViewModel
class CreateTimerViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var timerRepository: TimerRepository
    private val TAG = CreateTimerViewModel::class.java.simpleName
    private var curCreateTimeStr = "000000"
    val currentTimerTimeLiveData: LiveData<Time>
        get() = _currentTimerTimeLiveData
    private val _currentTimerTimeLiveData: MutableLiveData<Time> =
        MutableLiveData(Time(ZERO, ZERO, ZERO))

    fun appendTime(number: String) {
        if ((number == "0" || number == ZERO) && curCreateTimeStr == "000000") return
        if (number.length > 2) return
        if (number.length == 2 && (curCreateTimeStr[0] != '0' || curCreateTimeStr[1] != '0')) return
        if (curCreateTimeStr[0] != '0') return
        curCreateTimeStr += number
        curCreateTimeStr = curCreateTimeStr.removeRange(0, number.length)
        _currentTimerTimeLiveData.value = Time(
            hours = curCreateTimeStr.substring(0, 2),
            minutes = curCreateTimeStr.substring(2, 4),
            seconds = curCreateTimeStr.substring(4, 6),
        )
    }

    fun deleteLastElementInCurrentCreateTime() {
        if (curCreateTimeStr == "000000") return
        curCreateTimeStr = "0" + curCreateTimeStr.substring(0, 5)
        _currentTimerTimeLiveData.value = Time(
            hours = curCreateTimeStr.substring(0, 2),
            minutes = curCreateTimeStr.substring(2, 4),
            seconds = curCreateTimeStr.substring(4, 6),
        )
    }

    suspend fun addAndStartTimer(time: Time) {
        val timerData = TimerData(
            System.currentTimeMillis(),
            time.toMilliSecond(),
            time.toMilliSecond(),
            TimerState.STOP
        )
        timerRepository.addTimerData(timerData)
        timerRepository.start(timerData.createTime)
    }

    companion object {
        val ZERO = "00"
    }
}