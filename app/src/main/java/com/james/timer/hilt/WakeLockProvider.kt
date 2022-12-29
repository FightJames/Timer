package com.james.timer.hilt

import android.content.Context
import com.james.timer.view.utils.WakeLockManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WakeLockManagerEntryPoint {
    fun wakeLockManager(): WakeLockManager
}

fun getWakeLockManager(appContext: Context) =
    EntryPointAccessors.fromApplication(appContext, WakeLockManagerEntryPoint::class.java).wakeLockManager()