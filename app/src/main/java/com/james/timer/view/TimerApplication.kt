package com.james.timer.view

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.os.Build
import android.util.Log
import com.james.timer.BuildConfig
import com.james.timer.hilt.getTimerRepository
import com.james.timer.hilt.getWakeLockManager
import com.james.timer.model.TimerState
import com.james.timer.utils.jobManager
import com.james.timer.view.service.TimerService
import comparator
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class TimerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree());
        }
        ApplicationLifecycleManager.init(this.applicationContext)
        this.registerActivityLifecycleCallbacks(ApplicationLifecycleManager)
        ApplicationLifecycleManager.registerApplicationLifecycleCallback(object : ApplicationLifecycleListener {
            override fun onApplicationStart(context: Context) {
                val wakeLockManager = getWakeLockManager(this@TimerApplication)
                wakeLockManager.releaseCPU()
                val intent = Intent(context, TimerService::class.java)
                applicationContext.stopService(intent)
            }

            override fun onApplicationStop(context: Context) {
                jobManager.launchSafely {
                    val runningTimers = getTimerRepository(this@TimerApplication).getAllTimerData().sortedWith(comparator).filter { it.state == TimerState.RUNNING  }
                    if (runningTimers.isEmpty()) return@launchSafely
                    val wakeLockManager = getWakeLockManager(this@TimerApplication)
                    wakeLockManager.lockCPU(runningTimers.last().currentCountDown)
                    val intent = Intent(context, TimerService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        applicationContext.startForegroundService(intent)
                    } else {
                        applicationContext.startService(intent)
                    }
                }
            }
        })
    }
}