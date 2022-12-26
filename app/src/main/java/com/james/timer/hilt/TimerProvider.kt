package com.james.timer.hilt

import android.content.Context
import com.james.timer.repository.TimerRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TimerRepositoryEntryPoint {
    fun timerRepository(): TimerRepository
}

fun getTimerRepository(appContext: Context) =
    EntryPointAccessors.fromApplication(appContext, TimerRepositoryEntryPoint::class.java).timerRepository()