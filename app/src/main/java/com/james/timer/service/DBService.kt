package com.james.timer.service

import com.james.timer.db.DBManager
import javax.inject.Inject

class DBService @Inject constructor(val dbManager: DBManager) {

    fun getTimerDao() = dbManager.getTimerDAO()
}