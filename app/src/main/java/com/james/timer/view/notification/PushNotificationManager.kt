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
import androidx.core.app.NotificationManagerCompat
import com.james.timer.R
import kotlin.random.Random

@SuppressLint("RemoteViewLayout")
object PushNotificationManager {
    private val set = HashSet<Int>()

    fun getCurrentNotificationCount(context: Context) = set.size

    fun createMutipleTimerNotification(
        context: Context,
        isUserCanCancel: Boolean
    ): Pair<Int, Notification> {
        val notificationLayout =
            RemoteViews(context.packageName, R.layout.notification_muiltiple_timer)
        var builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_hourglass_bottom_24)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(!isUserCanCancel)
        createNotificationChannel(context)
        var id = Random.nextInt()
        while (set.contains(id)) {
            id = Random.nextInt()
        }
        val notification = builder.build()
        return Pair(id, notification)
    }

    fun cancelNotification(context: Context, id: Int) {
        if (!set.contains(id)) return
        NotificationManagerCompat.from(context).cancel(id)
        set.remove(id)
    }

    fun cancelAllNotification(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
        set.clear()
    }

    @SuppressLint("ServiceCast")
    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}