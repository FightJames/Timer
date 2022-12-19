package timer

import cancelChildren
import com.james.timer.utils.JobManagerImpl
import io
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Timer(val time: Long, private var currentTime: Long = time) {

    private val _currentTimeFlow = MutableStateFlow(currentTime)
    private val coroutineScope = JobManagerImpl(SupervisorJob() + io())
    private var timerJob: Job = CompletableDeferred(Unit)
    val currentTimeFlow: StateFlow<Long>
        get() = _currentTimeFlow

    @Volatile
    private var flag = false
    private val delayMiliSecond = 1000L

    fun start() {
        startInternal()
    }

    fun resume() {
        startInternal()
    }

    @Synchronized
    fun pause() {
        flag = false
        coroutineScope.cancelChildren()
    }

    @Synchronized
    fun stop() {
        flag = false
        coroutineScope.cancelChildren()
        currentTime = time
    }

    @Synchronized
    private fun startInternal() {
        if (timerJob.isActive) return
        timerJob = coroutineScope.launchSafely {
            flag = true
            while (flag) {
                delay(delayMiliSecond)
                currentTime -= 1000
                _currentTimeFlow.compareAndSet(_currentTimeFlow.value, currentTime)
            }
        }
    }
}