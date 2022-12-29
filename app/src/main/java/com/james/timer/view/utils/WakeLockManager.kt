package com.james.timer.view.utils

import android.content.Context
import android.os.PowerManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WakeLockManager @Inject constructor(@ApplicationContext context: Context) {
    private val LOCK_TAG = "com.james.timer:LOCK"
    private val wakeLock: PowerManager.WakeLock =
        (context.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_TAG)
        }

    fun lockCPU() {
        if (!wakeLock.isHeld) {
            wakeLock.acquire()
        }
    }

    fun releaseCPU() {
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
    }
}