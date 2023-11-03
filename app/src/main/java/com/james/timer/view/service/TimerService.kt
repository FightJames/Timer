package com.james.timer.view.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import com.james.timer.R
import com.james.timer.model.TimerState
import com.james.timer.repository.TimerRepository
import com.james.timer.utils.JobManagerImpl
import com.james.timer.view.MainActivity
import com.james.timer.view.notification.PushNotificationManager
import comparator
import dagger.hilt.android.AndroidEntryPoint
import io
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import milliSecondToTimeString
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    lateinit var timerRepository: TimerRepository

    var id: Int = -1
    lateinit var notification: Notification
    lateinit var view: RemoteViews

    companion object {
        const val ONGOING_NOTIFICATION_ID = 1001
    }

    val coroutineScrope = JobManagerImpl(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(
                    this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
        view =
            RemoteViews(this.packageName, R.layout.notification_muiltiple_timer)
        val notification = PushNotificationManager.createMutipleTimerNotification(
            this,
            view,
            false,
            pendingIntent
        )
        this.notification = notification
        PushNotificationManager.notifyNotification(
            this@TimerService,
            ONGOING_NOTIFICATION_ID,
            notification
        )
    }

    @SuppressLint("RemoteViewLayout")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Timber.d("JobService onStartCommand")
            startForeground(ONGOING_NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(ONGOING_NOTIFICATION_ID, notification)
        }
        initUI()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initUI() {
        coroutineScrope.launchSafely(context = io()) {
            val currentTimers = timerRepository.getAllTimerData().sortedWith(comparator)
                .filter { it.state == TimerState.RUNNING }
            withContext(Dispatchers.Main) {
                view.setTextViewText(
                    R.id.notificationTimerCountText,
                    "${currentTimers.size} timers"
                )
                view.setTextViewText(
                    R.id.notificationTimerText,
                    milliSecondToTimeString(timerRepository.getTimerCurrentTime(currentTimers[0].createTime))
                )
                timerRepository.getTimerCurrentTimeFlow(currentTimers[0].createTime)
                    .collect {
//                        Timber.d("Timer count down ${milliSecondToTimeString(it)}")
                        view.setTextViewText(
                            R.id.notificationTimerText,
                            milliSecondToTimeString(it)
                        )
                        PushNotificationManager.notifyNotification(
                            this@TimerService,
                            ONGOING_NOTIFICATION_ID,
                            notification
                        )
                    }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScrope.cancel()
    }
}