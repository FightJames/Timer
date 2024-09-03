package com.james.timer.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.PowerManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val mediaPlayer: MediaPlayer by lazy {
        val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        return@lazy MediaPlayer().let { mp ->
            val attributes = AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_ALARM)
                .build()
            mp.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
            mp.setAudioAttributes(attributes)
            mp.setDataSource(context, ringtoneUri)
            mp.isLooping = true
            mp
        }
    }
    private val timers = HashSet<String>()

    @Synchronized
    fun startRinging(timerCreateTime: String) {
        if (!timers.contains(timerCreateTime)) {
            if (timers.isEmpty()) {
                val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                am.mode = AudioManager.MODE_NORMAL
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                }
            }
            timers.add(timerCreateTime)
        }
    }

    @Synchronized
    fun stopRinging(timerCreateTime: String) {
        timers.remove(timerCreateTime)
        if (timers.size == 0) {
            mediaPlayer.stop()
        }
    }
}