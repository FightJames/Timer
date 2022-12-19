package com.james.timer.repository

import com.james.timer.model.Timer
import com.james.timer.service.DBService
import dagger.hilt.android.scopes.ViewModelScoped
import io
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class TimerRepository @Inject constructor(val service: DBService) {
    suspend fun saveTimer(timer: Timer) = withContext(io()) {
        service.saveTimer(timer)
        Timber.d("all timer ${service.getAllTimers().toString()}")
    }
}