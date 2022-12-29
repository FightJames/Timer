package com.james.timer.utils

import android.content.Context
import com.james.timer.R
import com.james.timer.model.Time

object StringFormatter {

    fun timerString(time: Time, context: Context) =
        context.resources.getString(R.string.time).let {
            String.format(it, time.hours, time.minutes, time.seconds)
        }
}