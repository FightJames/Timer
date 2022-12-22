package com.james.timer.view.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.james.timer.repository.TimerRepository
import com.james.timer.view.notification.PushNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    lateinit var timerRepository: TimerRepository

    var id: Int = -1
    lateinit var notification: Notification

    companion object {
        const val ONGOING_NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
//        val pendingIntent: PendingIntent =
//            Intent(this, ExampleActivity::class.java).let { notificationIntent ->
//                PendingIntent.getActivity(this, 0, notificationIntent,
//                    PendingIntent.FLAG_IMMUTABLE)
//            }
        Timber.d("James createNotification")
        val (id, notification) = PushNotificationManager.createMutipleTimerNotification(this, false)
        this.id = id
        this.notification = notification
    }

    @SuppressLint("RemoteViewLayout")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("James onStartCommand")
//        PushNotificationManager.cancelNotification(this, id)
//        stopForeground(true)
        startForeground(ONGOING_NOTIFICATION_ID, notification)
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}