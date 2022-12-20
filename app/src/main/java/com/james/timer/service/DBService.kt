package com.james.timer.service

import com.james.timer.db.DBManager
import com.james.timer.model.TimerData
import javax.inject.Inject

class DBService @Inject constructor(val dbManager: DBManager) {

    suspend fun saveTimer(timerData: TimerData) = dbManager.getTimerDAO().insert(timerData)

    suspend fun getAllTimers(): List<TimerData> = dbManager.getTimerDAO().getAll()
}