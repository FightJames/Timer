package com.james.timer.view

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import com.james.timer.BuildConfig
import com.james.timer.view.service.TimerService
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
            }

            override fun onApplicationStop(context: Context) {
                Timber.d("James onApplicationStop")
                val intent = Intent(context, TimerService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    applicationContext.startForegroundService(intent)
                } else {
                    applicationContext.startService(intent)
                }
            }

        })
    }
}