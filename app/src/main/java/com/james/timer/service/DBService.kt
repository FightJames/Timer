package com.james.timer.service

import com.james.timer.db.DBManager
import com.james.timer.model.Timer
import javax.inject.Inject

class DBService @Inject constructor(val dbManager: DBManager) {

    suspend fun saveTimer(timer: Timer) = dbManager.getTimerDAO().insert(timer)

    suspend fun getAllTimers(): List<Timer> = dbManager.getTimerDAO().getAll()
}