package com.james.timer.repository

import com.james.timer.db.DBManager
import com.james.timer.model.TimerData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DBRepository @Inject constructor(val dbManager: DBManager) {

    suspend fun insertTimerData(timerData: TimerData) = dbManager.getTimerDAO().insert(timerData)

    suspend fun updateTimerData(timerData: TimerData) = dbManager.getTimerDAO().updateTimerData(timerData)

    suspend fun deleteTimerData(timerData: TimerData) = dbManager.getTimerDAO().deleteTimerData(timerData)

    suspend fun getAllTimersData(): List<TimerData> = dbManager.getTimerDAO().getAll()
}