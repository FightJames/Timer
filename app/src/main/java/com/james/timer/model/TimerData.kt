package com.james.timer.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.TEXT
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.james.timer.db.TimerDB

@Keep
@Entity(tableName = TimerDB.TIMER_TABLE_NAME)
data class TimerData (
    @PrimaryKey
    @ColumnInfo(name = "create_time", typeAffinity = TEXT)
    val createTime: Long,

    @ColumnInfo(name = "count_down_time", typeAffinity = TEXT)
    val countDownTime: Long,

    @ColumnInfo(name = "current_count_down", typeAffinity = TEXT)
    var currentCountDown: Long,

    @ColumnInfo(name = "state")
    var state: TimerState,
)

@Keep
enum class TimerState(val value: Int) {
    STOP(0), PAUSE(1), RUNNING(2)
}