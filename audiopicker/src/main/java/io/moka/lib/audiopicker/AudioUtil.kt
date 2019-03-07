package io.moka.lib.audiopicker


import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Vibrator
import io.moka.lib.base.MokaBase
import io.moka.lib.base.util.MediaUtil
import io.moka.lib.base.util.supVibrate


object AudioUtil {

    private val vibrator by lazy { MokaBase.context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    fun vibrate() {
        vibrator.supVibrate(longArrayOf(1000, 200, 200, 200), 0)
    }

    fun playPlayer(sourceString: String = "", streamType: Int = AudioManager.STREAM_ALARM) {
        MediaUtil.Player()
                .setIsLoop(true)
                .setSourceString(sourceString)
                .setStreamType(streamType)
                .setWakeLock(false)
                .play()
    }

    fun playPlayer(uri: Uri, streamType: Int = AudioManager.STREAM_ALARM) {
        MediaUtil.Player()
                .setIsLoop(true)
                .setSourceUri(uri)
                .setStreamType(streamType)
                .setWakeLock(false)
                .play()
    }

    fun stopPlayer() {
        MediaUtil.stop()
        vibrator.cancel()
    }

    fun releasePlayer() {
        MediaUtil.release()
    }

    /**
     */

    fun volumnUP(streamType: Int) {
        MediaUtil.volumnUP(streamType)
    }

    fun volumnDOWN(streamType: Int) {
        MediaUtil.volumnDOWN(streamType)
    }

    fun getCurrentVol(streamType: Int): Int {
        return MediaUtil.getCurrentVol(streamType)
    }

    fun setCurrentVol(vol: Int, streamType: Int) {
        MediaUtil.setCurrentVol(vol, streamType)
    }

    fun getMaxVol(streamType: Int = AudioManager.STREAM_ALARM): Int {
        return MediaUtil.getMaxVol(streamType)
    }

    fun stopVibrator() {
        vibrator.cancel()
    }

    fun checkAudioRunning(): Boolean {
        return MediaUtil.isMusicActive()
    }

}
