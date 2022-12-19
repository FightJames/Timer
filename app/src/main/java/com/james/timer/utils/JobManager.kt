package com.james.timer.utils

import io
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


class SafeExceptionHandler {
    fun handle(tag: String, t: Throwable) {
        Timber.e("JobManagerImpl error $t")
    }
}

val safeExceptionHandler = SafeExceptionHandler()
val SafeCoroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
    val tag = coroutineContext[CoroutineName]?.name ?: "unknown"
    safeExceptionHandler.handle(tag, throwable)
}

class JobManagerImpl(
    coroutineContext: CoroutineContext,
    private val defaultCoroutineExceptionHandler: CoroutineExceptionHandler = SafeCoroutineExceptionHandler
) : CoroutineScope {

    override val coroutineContext: CoroutineContext = coroutineContext + CoroutineName("JobManager")

    override fun toString(): String = "CoroutineScope(coroutineContext=$coroutineContext)"

    /**
     * This launch will not throw exception, this the [block] raise the exception,
     * the exception will handle by CoroutineExceptionHandler,
     * if the CEH is not found in the currentContext,
     * use [SafeCoroutineExceptionHandler] instead
     *
     * @param name: name for this coroutine, default is UnKnown
     *  this name will save as [CoroutineName]
     * @param context @see [CoroutineScope.launch]
     * @param start @see [CoroutineScope.launch]
     * @param delayTime delay time before execute [block]
     * @param block @see [CoroutineScope.launch]
     */
    fun launchSafely(
        name: Any = "unknown",
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        delayTime: Long = 0L,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        val type = coroutineContext[ContinuationInterceptor].toString()
        val coroutineName = CoroutineName("$name  $type")
        return launch(newSafeCoroutineContext(context, coroutineName), start) {
            try {
                delay(delayTime)
                coroutineScope {
                    block()
                }
            } catch (t: Throwable) {
                t.rethrowOnCancellation()
                coroutineContext[CoroutineExceptionHandler.Key]?.handleException(coroutineContext, t)
                t.printStackTrace()
            }
        }
    }

    /**
     * **[defaultCoroutineExceptionHandler] before then context**
     *  because of it is allow [context] with a [CoroutineExceptionHandler] can override the [defaultCoroutineExceptionHandler]
     */
    private fun newSafeCoroutineContext(context: CoroutineContext, coroutineName: CoroutineName) =
        defaultCoroutineExceptionHandler + context + coroutineName
}

val jobManager = JobManagerImpl(SupervisorJob() + io())

fun Throwable.rethrowOnCancellation(action: ((t: Throwable) -> Unit)? = null) {
    action?.invoke(this)
    if (this is CancellationException) throw this
}
