package io.moka.mokabaselib.util

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import io.moka.mokabaselib.MokaBase.context
import java.io.File

object MediaUtil {

    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null

    private var streamType = AudioManager.STREAM_MUSIC

    /*
    음악 플레이
     */
    fun play(notificationUri: String? = null, streamType: Int = AudioManager.STREAM_MUSIC) {
        stop()
        release()

        mediaPlayer = MediaPlayer()
        val mediaPlayer = mediaPlayer!!

        /* initialize */
        mediaPlayer.reset()
        mediaPlayer.supSetStreamType(streamType)

        /* set notification song  */
        val willPlayUri: Uri = checkUri(notificationUri)
        mediaPlayer.setDataSource(context, willPlayUri)

        /* set loop */
        mediaPlayer.isLooping = false

        /* set listener */
        mediaPlayer.setOnCompletionListener {}
        mediaPlayer.setOnPreparedListener { mediaPlayer.start() }
        mediaPlayer.setOnErrorListener { _, _, _ ->
            stop()
            release()
            return@setOnErrorListener true
        }

        /* play async */
        mediaPlayer.prepareAsync()
    }

    private fun checkUri(notificationUri: String?): Uri {
        return if (notificationUri.isNullOrEmpty()) {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }
        else {
            Uri.parse(notificationUri)
        }
    }

    /*
    음악 중지
     */
    fun stop() {
        if (mediaPlayer?.isPlaying == true)
            mediaPlayer?.stop()
    }

    /*
    메모리에서 해제
     */
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     */

    fun volumnUP(streamType: Int = MediaUtil.streamType) {
        setAudioManager()

        val currentVol = audioManager!!.getStreamVolume(streamType)
        val maxVol = audioManager!!.getStreamMaxVolume(streamType)

        if (currentVol < maxVol)
            audioManager!!.setStreamVolume(streamType, currentVol + 1, AudioManager.FLAG_PLAY_SOUND)
    }

    fun volumnDOWN(streamType: Int = MediaUtil.streamType) {
        setAudioManager()

        val currVol = audioManager!!.getStreamVolume(streamType)

        if (currVol > 0)
            audioManager!!.setStreamVolume(streamType, currVol - 1, AudioManager.FLAG_PLAY_SOUND)
    }

    fun getCurrentVol(streamType: Int = MediaUtil.streamType): Int {
        setAudioManager()
        return audioManager!!.getStreamVolume(streamType)
    }

    fun setCurrentVol(vol: Int, streamType: Int = MediaUtil.streamType) {
        setAudioManager()
        val maxVol = audioManager!!.getStreamMaxVolume(streamType)

        if (vol <= maxVol)
            audioManager!!.setStreamVolume(streamType, vol, AudioManager.FLAG_PLAY_SOUND)
    }

    fun getMaxVol(streamType: Int = MediaUtil.streamType): Int {
        setAudioManager()
        return audioManager!!.getStreamMaxVolume(streamType)
    }

    private fun setAudioManager(): AudioManager {
        if (null == audioManager && null != context)
            audioManager = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return audioManager!!
    }

    fun unmuteSound() {
        supSetStreamMute(AudioManager.STREAM_MUSIC, false)
    }

    fun muteSound() {
        supSetStreamMute(AudioManager.STREAM_MUSIC, true)
    }

    fun isMusicActive(): Boolean {
        setAudioManager()
        return audioManager!!.isMusicActive
    }

    fun requestFocus() {
        setAudioManager()
        audioManager!!.supRequestAudioFocus()
    }

    fun abandonFocus() {
        setAudioManager()
        audioManager!!.supAnandonAudioFocus()
    }

}