package com.james.timer.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.james.timer.model.TimerData

@Dao
interface TimerDao {

    @Query("SELECT * FROM ${TimerDB.TIMER_TABLE_NAME}")
    suspend fun getAll(): List<TimerData>

    @Insert
    suspend fun insert(timerData: TimerData)

    @Insert
    suspend fun insertAll(vararg timerData: TimerData)

    @Update
    suspend fun updateTimerData(timerData: TimerData)

    @Delete
    suspend fun deleteTimerData(timerData: TimerData)

}