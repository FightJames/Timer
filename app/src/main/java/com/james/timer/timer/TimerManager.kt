package com.james.timer.timer

import com.james.timer.model.TimerData
import com.james.timer.repository.DBRepository
import com.james.timer.utils.SoundManager
import com.james.timer.utils.jobManager
import io
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerManager {
    private val dbRepository: DBRepository
    private var initJob: Job = CompletableDeferred(Unit)

    private val map: MutableMap<Long, Timer> = HashMap()
    private val soundManager: SoundManager

    @Inject
    constructor(dbRepository: DBRepository, soundManager: SoundManager) {
        this.soundManager = soundManager
        this.dbRepository = dbRepository
        initJob = jobManager.launchSafely(context = io()) {
            val timersInStorage = dbRepository.getAllTimersData()
            timersInStorage.forEach {
                val timer = TimerImpl(it, dbRepository, soundManager)
                map[it.createTime] = timer
            }
        }
    }

    suspend fun getTimerDataList(): List<TimerData> {
        initJob.join()
        return map.values.map { it.timerData }.toList()
    }

    suspend fun addTimerData(timerData: TimerData) = withContext(io()) {
        initJob.join()
        dbRepository.insertTimerData(timerData)
        val timer = TimerImpl(timerData, dbRepository, soundManager)
        map[timerData.createTime] = timer
    }

    suspend fun deleteTimerData(timerData: TimerData) = withContext(io()) {
        initJob.join()
        map[timerData.createTime]?.stop()
        map.remove(timerData.createTime)
        dbRepository.deleteTimerData(timerData)
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