package com.james.timer.view

import android.content.Context

interface ApplicationLifecycleListener {

    fun onApplicationStart(context: Context)

    fun onApplicationStop(context: Context)
}