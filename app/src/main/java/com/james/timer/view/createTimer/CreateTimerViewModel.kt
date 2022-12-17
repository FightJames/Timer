package com.james.timer.view.createTimer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.james.timer.model.CreateTime

class CreateTimerViewModel() : ViewModel() {
    private var curCreateTimeStr = "000000"
    val currentTimerTimeLiveData: LiveData<CreateTime>
        get() = _currentTimerTimeLiveData
    private val _currentTimerTimeLiveData: MutableLiveData<CreateTime> =
        MutableLiveData(CreateTime())

    fun appendTime(number: String) {
        if (number == "0" || number == "00" && curCreateTimeStr == "000000") return
        if (number.length > 2) return
        if (number.length == 2 && curCreateTimeStr[0] != '0' && curCreateTimeStr[1] != '0') return
        if (curCreateTimeStr[0] != '0') return
        curCreateTimeStr += number
        curCreateTimeStr.removeRange(0, number.length)
        _currentTimerTimeLiveData.value = CreateTime(
            hours = curCreateTimeStr.substring(0, 2),
            minutes = curCreateTimeStr.substring(2, 4),
            seconds = curCreateTimeStr.substring(4, 6),
        )
    }

    fun deleteLastElementInCurrentCreateTime() {
        if (curCreateTimeStr == "000000") return
        curCreateTimeStr = "0" + curCreateTimeStr.substring(0, 5)
        _currentTimerTimeLiveData.value = CreateTime(
            hours = curCreateTimeStr.substring(0, 2),
            minutes = curCreateTimeStr.substring(2, 4),
            seconds = curCreateTimeStr.substring(4, 6),
        )
    }
}