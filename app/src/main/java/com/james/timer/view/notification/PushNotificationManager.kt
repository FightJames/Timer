package com.james.timer.view.notification

import NOTIFICATION_CHANNEL_ID
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import com.james.timer.R


@SuppressLint("RemoteViewLayout")
object PushNotificationManager {

    fun createMutipleTimerNotification(
        context: Context,
        view: RemoteViews,
        isUserCanCancel: Boolean
    ): Notification {
        var builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_hourglass_bottom_24)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(view)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setShowWhen(false)
            .setWhen(0L)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setOngoing(!isUserCanCancel)
            .setVibrate(LongArray(1).apply { this[0] = -1L })
            .setOnlyAlertOnce(true)
        createNotificationChannel(context)
        val notification = builder.build()
        return notification
    }

    fun notifyNotification(context: Context, id: Int, notification: Notification) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(id, notification)
    }

    @SuppressLint("ServiceCast")
    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            channel.vibrationPattern = null
            channel.enableVibration(false)
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}