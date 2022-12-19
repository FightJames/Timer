import com.james.timer.model.Time
import kotlinx.coroutines.Dispatchers

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