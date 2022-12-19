package com.james.timer.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.james.timer.model.Timer

@Dao
interface TimerDao {

    @Query("SELECT * FROM ${TimerDB.TIMER_TABLE_NAME}")
    suspend fun getAll(): List<Timer>

    @Insert
    suspend fun insert(timer: Timer)

    @Insert
    suspend fun insertAll(vararg timer: Timer)
}