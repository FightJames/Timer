package com.james.timer.service

import com.james.timer.db.DBManager
import com.james.timer.model.TimerData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DBService @Inject constructor(val dbManager: DBManager) {

    suspend fun saveTimerData(timerData: TimerData) = dbManager.getTimerDAO().insert(timerData)

    suspend fun updateTimerData(timerData: TimerData) = dbManager.getTimerDAO().updateTimerData(timerData)

    suspend fun deleteTimerData(timerData: TimerData) = dbManager.getTimerDAO().deleteTimerData(timerData)

    suspend fun getAllTimersData(): List<TimerData> = dbManager.getTimerDAO().getAll()
}