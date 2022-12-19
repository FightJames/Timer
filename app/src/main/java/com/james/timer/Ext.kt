import com.james.timer.model.Time
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren

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

fun CoroutineScope.cancelChildren() {
    this.coroutineContext[Job]?.cancelChildren()
}