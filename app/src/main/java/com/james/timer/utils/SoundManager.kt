package com.james.timer.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val mediaPlayer: MediaPlayer by lazy {
        return@lazy MediaPlayer().let { mp ->
            val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            try {
                mp.setDataSource(context, ringtoneUri)
                val attributes = AudioAttributes.Builder()
                    .setLegacyStreamType(AudioManager.STREAM_ALARM)
                    .build()
                mp.setAudioAttributes(attributes)
                mp.isLooping = true
                mp.prepare()
            } catch (e: Exception) {
            }
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
                mediaPlayer.seekTo(0)
                mediaPlayer.start()
            }
            timers.add(timerCreateTime)
        }
    }

    @Synchronized
    fun stopRinging(timerCreateTime: String) {
        timers.remove(timerCreateTime)
        if (timers.size == 0) {
            mediaPlayer.pause()
        }
    }
}