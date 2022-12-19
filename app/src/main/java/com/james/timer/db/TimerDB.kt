package com.james.timer.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.james.timer.model.Timer

@Database(entities = [Timer::class], version = 1)
@TypeConverters(TimerDBConverters::class)
abstract class TimerDB : RoomDatabase() {
    abstract fun timerDao(): TimerDao

    companion object {
        const val NAME = "timer-database"
        const val TIMER_TABLE_NAME = "Timer"
    }
}

