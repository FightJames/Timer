package com.james.timer.timer

import com.james.timer.model.TimerData
import com.james.timer.model.TimerState
import com.james.timer.service.DBService
import com.james.timer.utils.jobManager
import io
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerManager {
    private val service: DBService
    private var initJob: Job = CompletableDeferred(Unit)
    private val _timerDataList: MutableList<TimerData> = mutableListOf()
    val timerDataList: List<TimerData>
        get() = _timerDataList
    private val map: MutableMap<Long, Pair<TimerData, Timer>> = HashMap()
    private val comparator = Comparator<TimerData> { p0, p1 ->
        val stateDiff = p1.state.value - p0.state.value
        if (stateDiff != 0) return@Comparator stateDiff
        val countDownDiff = p0.currentCountDown - p1.currentCountDown
        if (countDownDiff != 0L) return@Comparator if (countDownDiff > 0L) 1 else -1
        val createTimeDiff = p0.createTime - p1.createTime
        return@Comparator when {
            createTimeDiff == 0L -> 0
            createTimeDiff > 0L -> 1
            else -> -1
        }
    }

    @Inject
    constructor(service: DBService) {
        this.service = service
        initJob = jobManager.launchSafely(context = io()) {
            val timersInStorage = service.getAllTimers()
            _timerDataList.addAll(timersInStorage)
            _timerDataList.sortWith(comparator)
            _timerDataList.forEach {
                val timer = Timer(it.countDownTime, it.currentCountDown)
                map[it.createTime] = Pair(it, timer)
                if (it.state == TimerState.RUNNING) timer.start()
            }
        }
    }

    suspend fun addTimerData(timerData: TimerData) = withContext(io()){
        initJob.join()
        _timerDataList.add(timerData)
        _timerDataList.sortWith(comparator)
        val timer = Timer(timerData.countDownTime, timerData.currentCountDown)
        map[timerData.createTime] = Pair(timerData, timer)
    }

    suspend fun deleteTimerData(timerData: TimerData) = withContext(io()) {
        initJob.join()
        _timerDataList.remove(timerData)
    }

    suspend fun getTimer(createTime: Long): Timer? {
        initJob.join()
        return map[createTime]?.second
    }

}