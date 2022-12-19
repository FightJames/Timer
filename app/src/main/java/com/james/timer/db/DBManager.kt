package com.james.timer.db

import android.content.Context
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DBManager @Inject constructor(@ApplicationContext applicationContext: Context) {
    private val timerDB: TimerDB by lazy {
        Room.databaseBuilder(
            applicationContext,
            TimerDB::class.java, TimerDB.NAME
        ).build()
    }

    fun getTimerDAO() = timerDB.timerDao()
}