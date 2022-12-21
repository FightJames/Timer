package com.james.timer.view.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.james.timer.model.TimerData
import com.james.timer.repository.TimerRepository
import comparator
import dagger.hilt.android.lifecycle.HiltViewModel
import io
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var timerRepository: TimerRepository
    private val TAG = TimerViewModel::class.java.simpleName
    private val _timerDataListLiveData = MutableLiveData<List<TimerData>>()
    val timerDataListLiveData: LiveData<List<TimerData>>
        get() = _timerDataListLiveData

    fun fetchTimerData() {
        viewModelScope.launch {
            _timerDataListLiveData.value = timerRepository.getAllTimerData().sortedWith(comparator)
        }
    }

    fun start(createTime: Long) {
        viewModelScope.launch {
            timerRepository.start(createTime)
            fetchTimerData()
        }
    }

    fun resume(createTime: Long) {
        viewModelScope.launch {
            timerRepository.resume(createTime)
            fetchTimerData()
        }
    }

    fun pause(createTime: Long) {
        viewModelScope.launch {
            timerRepository.pause(createTime)
            fetchTimerData()
        }
    }

    fun stop(createTime: Long) {
        viewModelScope.launch {
            timerRepository.stop(createTime)
            fetchTimerData()
        }
    }

    fun removeTimer(timerData: TimerData) {
        viewModelScope.launch {
            timerRepository.removeTimer(timerData)
            fetchTimerData()
        }
    }

    suspend fun subscribeTimer(createTime: Long, collector: FlowCollector<Long>) {
        timerRepository.subscribeTimerCurrentTime(createTime, collector)
    }

    suspend fun getTimerCurrentTime(createTime: Long): Long =
        timerRepository.getTimerCurrentTime(createTime)

    override fun onCleared() {
        super.onCleared()
    }
}