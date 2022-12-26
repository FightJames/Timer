package com.james.timer.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.james.timer.utils.jobManager
import kotlinx.coroutines.Dispatchers

@SuppressLint("StaticFieldLeak")
object ApplicationLifecycleManager : Application.ActivityLifecycleCallbacks {
    private var count: Int = 0
    private val callbacks = mutableListOf<ApplicationLifecycleListener>()
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }

    fun registerApplicationLifecycleCallback(callback: ApplicationLifecycleListener) {
        callbacks.add(callback)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(p0: Activity) {
        count++
        if (count == 1) {
            callbacks.forEach {
                it.onApplicationStart(context)
            }
        }
    }

    override fun onActivityResumed(p0: Activity) {
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
        count--
        jobManager.launchSafely(context = Dispatchers.Main) {
            if (count == 0) {
                callbacks.forEach {
                    it.onApplicationStop(context)
                }
            }
        }
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }
}