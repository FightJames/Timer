package com.james.timer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.james.timer.db.TimerDB

@Entity(tableName = TimerDB.TIMER_TABLE_NAME)
data class TimerData (
    @PrimaryKey
    @ColumnInfo(name = "create_time", typeAffinity = 2)
    val createTime: Long,

    @ColumnInfo(name = "count_down_time", typeAffinity = 2)
    val countDownTime: Long,

    @ColumnInfo(name = "current_count_down", typeAffinity = 2)
    val currentCountDown: Long,

    @ColumnInfo(name = "state")
    val state: TimerState,
)

enum class TimerState(val value: Int) {
    STOP(0), PAUSE(1), RUNNING(2)
}