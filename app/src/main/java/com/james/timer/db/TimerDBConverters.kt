package com.james.timer.db

import androidx.room.TypeConverter

class TimerDBConverters {

    @TypeConverter
    fun longToString(value: Long): String = value.toString()

    @TypeConverter
    fun stringToLong(input: String): Long =
        try {
            input.toLong()
        } catch (t: Throwable) {
            0L
        }
}