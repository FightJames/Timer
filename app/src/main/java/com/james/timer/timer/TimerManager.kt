package com.james.timer.timer

import com.james.timer.model.TimerData
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

    private val timerDataList: MutableList<TimerData> = mutableListOf()
    private val map: MutableMap<Long, Timer> = HashMap()

    @Inject
    constructor(service: DBService) {
        this.service = service
        initJob = jobManager.launchSafely(context = io()) {
            val timersInStorage = service.getAllTimersData()
            timerDataList.addAll(timersInStorage)
            timersInStorage.forEach {
                val timer = TimerImpl(it, service)
                map[it.createTime] = timer
            }
        }
    }

    suspend fun getTimerDataList(): List<TimerData> {
        initJob.join()
        return timerDataList.toList()
    }

    suspend fun addTimerData(timerData: TimerData) = withContext(io()) {
        initJob.join()
        timerDataList.add(timerData)
        service.insertTimerData(timerData)
        val timer = TimerImpl(timerData, service)
        map[timerData.createTime] = timer
    }

    suspend fun deleteTimerData(timerData: TimerData) = withContext(io()) {
        initJob.join()
        map.remove(timerData.createTime)
        timerDataList.remove(timerData)
        service.deleteTimerData(timerData)
    }

    suspend fun getTimer(createTime: Long): Timer {
        initJob.join()
        return map[createTime] ?: throw Exception("Plz call add timer before getting it")
    }

    suspend fun getTimerData(createTime: Long): TimerData? {
        initJob.join()
        return map[createTime]?.timerData
    }
}