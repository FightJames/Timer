import android.os.SystemClock
import android.view.View
import com.james.timer.model.Time
import com.james.timer.model.TimerData
import com.james.timer.utils.JobManagerImpl
import kotlinx.coroutines.*
import java.util.concurrent.Executors

fun io() = Dispatchers.IO
fun default() = Dispatchers.Default
fun main() = Dispatchers.Main
fun mainImmediate() = Dispatchers.Main.immediate


fun Time.toMilliSecond(): Long {
    val h = this.hours.toLongOrNull() ?: 0L
    val m = this.minutes.toLongOrNull() ?: 0L
    val s = this.seconds.toLongOrNull() ?: 0L
    return h * 60 * 60 * 1000 + m * 60 * 1000 + s * 1000
}

fun milliSecondToTimeString(milliSecond: Long): String {
    var _milliSecond = milliSecond
    if (milliSecond < 0L) _milliSecond *= -1
    val h = (_milliSecond / (60 * 60 * 1000)).toInt()
    _milliSecond %= (60 * 60 * 1000)
    val m = (_milliSecond / (60 * 1000)).toInt()
    _milliSecond %= (60 * 1000)
    val s = (_milliSecond / 1000).toInt()

    return if (milliSecond < 0L) {
        "-${String.format("%02d:%02d:%02d",h,m,s)}"
    } else {
        String.format("%02d:%02d:%02d",h,m,s)
    }
}

fun CoroutineScope.cancelChildren() {
    this.coroutineContext[Job]?.cancelChildren()
}

val comparator = Comparator<TimerData> { p0, p1 ->
    val stateDiff = p1.state.value - p0.state.value
    if (stateDiff != 0) return@Comparator stateDiff
    val countDownDiff = p0.currentCountDown - p1.currentCountDown
    if (countDownDiff != 0L) return@Comparator if (countDownDiff > 0L) 1 else -1
    val createTimeDiff = p0.createTime - p1.createTime
    return@Comparator when {
        createTimeDiff == 0L -> 0
        createTimeDiff > 0L -> 1
        else -> -1
    }
}

fun View.clickWithDebounce(debounceTime: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

val serialJobManager = JobManagerImpl(SupervisorJob() + Executors.newSingleThreadExecutor().asCoroutineDispatcher())