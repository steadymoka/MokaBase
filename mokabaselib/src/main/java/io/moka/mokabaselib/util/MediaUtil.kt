package io.moka.mokabaselib.util

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.PowerManager
import io.moka.mokabaselib.MokaBase.context

object MediaUtil {

    class Player {
        private var sourceString: String? = null
        private var sourceUri: Uri? = null
        private var streamType: Int = AudioManager.STREAM_MUSIC
        private var isLoop: Boolean = false
        private var wakeLock: Boolean = false

        fun setSourceString(sourceString: String): Player {
            this.sourceString = sourceString
            return this
        }

        fun setSourceUri(sourceUri: Uri): Player {
            this.sourceUri = sourceUri
            return this
        }

        fun setStreamType(streamType: Int): Player {
            this.streamType = streamType
            return this
        }

        fun setIsLoop(isLoop: Boolean): Player {
            this.isLoop = isLoop
            return this
        }

        fun setWakeLock(wakeLock: Boolean): Player {
            this.wakeLock = wakeLock
            return this
        }

        fun play() {
            MediaUtil.play(sourceString, sourceUri, streamType, isLoop, wakeLock)
        }
    }

    /**
     * Media Play
     */

    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null

    fun play(
            sourceString: String? = null,
            sourceUri: Uri? = null,
            streamType: Int = AudioManager.STREAM_MUSIC,
            isLoop: Boolean = false,
            wakeLock: Boolean = false) {

        stop()
        release()

        mediaPlayer = MediaPlayer()
        val mediaPlayer = mediaPlayer!!

        /* initialize */
        mediaPlayer.reset()
        mediaPlayer.supSetStreamType(streamType)

        /* set notification song  */
        if (null == sourceUri) {
            val willPlayUri: Uri = checkUri(sourceString)
            mediaPlayer.setDataSource(context, willPlayUri)
        }
        else {
            mediaPlayer.setDataSource(context, sourceUri)
        }

        /* set loop */
        mediaPlayer.isLooping = isLoop

        /* wake lock */
        if (wakeLock)
            mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)

        /* set listener */
        mediaPlayer.setOnCompletionListener {}
        mediaPlayer.setOnPreparedListener { mediaPlayer.start() }

        /* handle error */
        var replayCount = 0
        mediaPlayer.setOnErrorListener { _, _, _ ->
            stop()
            release()

            /*
            총 3번의 재시도를 실시한다.
             */
            if (replayCount < 3) {
                replayCount++
                if (replayCount == 2)
                    play(null, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), streamType, isLoop, wakeLock)
                else
                    play(sourceString, null, streamType, isLoop, wakeLock)
            }
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

    fun volumnUP(streamType: Int = AudioManager.STREAM_MUSIC) {
        setAudioManager()

        val currentVol = audioManager!!.getStreamVolume(streamType)
        val maxVol = audioManager!!.getStreamMaxVolume(streamType)

        if (currentVol < maxVol)
            audioManager!!.setStreamVolume(streamType, currentVol + 1, AudioManager.FLAG_PLAY_SOUND)
    }

    fun volumnDOWN(streamType: Int = AudioManager.STREAM_MUSIC) {
        setAudioManager()

        val currVol = audioManager!!.getStreamVolume(streamType)

        if (currVol > 0)
            audioManager!!.setStreamVolume(streamType, currVol - 1, AudioManager.FLAG_PLAY_SOUND)
    }

    fun getCurrentVol(streamType: Int = AudioManager.STREAM_MUSIC): Int {
        setAudioManager()
        return audioManager!!.getStreamVolume(streamType)
    }

    fun setCurrentVol(vol: Int, streamType: Int = AudioManager.STREAM_MUSIC) {
        setAudioManager()
        val maxVol = audioManager!!.getStreamMaxVolume(streamType)

        if (vol <= maxVol)
            audioManager!!.setStreamVolume(streamType, vol, AudioManager.FLAG_PLAY_SOUND)
    }

    fun getMaxVol(streamType: Int = AudioManager.STREAM_MUSIC): Int {
        setAudioManager()
        return audioManager!!.getStreamMaxVolume(streamType)
    }

    private fun setAudioManager(): AudioManager {
        if (null == audioManager && null != context)
            audioManager = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return audioManager!!
    }

    fun unmuteSound(streamType: Int = AudioManager.STREAM_MUSIC) {
        supSetStreamMute(AudioManager.STREAM_MUSIC, false)
    }

    fun muteSound(streamType: Int = AudioManager.STREAM_MUSIC) {
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