package com.james.timer.repository

import com.james.timer.model.Timer
import com.james.timer.service.DBService
import dagger.hilt.android.scopes.ViewModelScoped
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class TimerRepository @Inject constructor(val service: DBService) {
    fun saveTimer(timer: Timer) {
        Timber.d("save Timer ${service.getTimerDao()}")
    }
}