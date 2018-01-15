package io.moka.mokabaselib.util

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import io.moka.mokabaselib.MokaBase.context

@SuppressLint("MissingPermission")
fun Vibrator.supVibrate(pattern: LongArray, repeat: Int) {
    if (pattern.isEmpty())
        pattern.plus(longArrayOf(0, 1000, 200, 1000, 2400))

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val vibrateEffect = VibrationEffect.createWaveform(pattern, repeat)
        vibrate(vibrateEffect)
    }
    else {
        vibrate(pattern, -1)
    }
}

fun supSetStreamMute(streamType: Int, isMute: Boolean) {
    if (null == context)
        return
    val aManager = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (isMute)
            aManager.adjustStreamVolume(streamType, AudioManager.ADJUST_MUTE, AudioManager.FLAG_PLAY_SOUND)
        else
            aManager.adjustStreamVolume(streamType, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_PLAY_SOUND)
    }
    else {
        aManager.setStreamMute(streamType, isMute)
    }

    /*
    https://developer.android.com/reference/android/media/AudioManager.html
    ___

    val FLAG_SHOW_UI = 1 shl 0

    /**
     * Whether to include ringer modes as possible options when changing volume.
     * For example, if true and volume level is 0 and the volume is adjusted
     * with {@link #ADJUST_LOWER}, then the ringer mode may switch the silent or
     * vibrate mode.
     * <p>
     * By default this is on for the ring stream. If this flag is included,
     * this behavior will be present regardless of the stream type being
     * affected by the ringer mode.
     *
     * @see #adjustVolume(int, int)
     * @see #adjustStreamVolume(int, int, int)
    */
    val FLAG_ALLOW_RINGER_MODES = 1 shl 1

    /**
     * Whether to play a sound when changing the volume.
     * <p>
     * If this is given to {@link #adjustVolume(int, int)} or
     * {@link #adjustSuggestedStreamVolume(int, int, int)}, it may be ignored
     * in some cases (for example, the decided stream type is not
     * {@link AudioManager#STREAM_RING}, or the volume is being adjusted
     * downward).
     *
     * @see #adjustStreamVolume(int, int, int)
     * @see #adjustVolume(int, int)
     * @see #setStreamVolume(int, int, int)
    */
    val FLAG_PLAY_SOUND = 1 shl 2

    /**
     * Removes any sounds/vibrate that may be in the queue, or are playing (related to
     * changing volume).
    */
    val FLAG_REMOVE_SOUND_AND_VIBRATE = 1 shl 3

    /**
     * Whether to vibrate if going into the vibrate ringer mode.
    */
    val FLAG_VIBRATE = 1 shl 4

    /**
     * Indicates to VolumePanel that the volume slider should be disabled as user
     * cannot change the stream volume
     * @hide
    */
    val FLAG_FIXED_VOLUME = 1 shl 5

    /**
     * Indicates the volume set/adjust call is for Bluetooth absolute volume
     * @hide
    */
    val FLAG_BLUETOOTH_ABS_VOLUME = 1 shl 6

    /**
     * Adjusting the volume was prevented due to silent mode, display a hint in the UI.
     * @hide
    */
    val FLAG_SHOW_SILENT_HINT = 1 shl 7

    /**
     * Indicates the volume call is for Hdmi Cec system audio volume
     * @hide
    */
    val FLAG_HDMI_SYSTEM_AUDIO_VOLUME = 1 shl 8

    /**
     * Indicates that this should only be handled if media is actively playing.
     * @hide
    */
    val FLAG_ACTIVE_MEDIA_ONLY = 1 shl 9

    /**
     * Like FLAG_SHOW_UI, but only dialog warnings and confirmations, no sliders.
     * @hide
    */
    val FLAG_SHOW_UI_WARNINGS = 1 shl 10

    /**
     * Adjusting the volume down from vibrated was prevented, display a hint in the UI.
     * @hide
    */
    val FLAG_SHOW_VIBRATE_HINT = 1 shl 11

    /**
     * Adjusting the volume due to a hardware key press.
     * @hide
    */
    val FLAG_FROM_KEY = 1 shl 12

     */
}

fun MediaPlayer.supSetStreamType(streamType: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val audioAttribute =
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setLegacyStreamType(streamType)
                        .build()

        setAudioAttributes(audioAttribute)
    }
    else {
        setAudioStreamType(streamType)
    }
}

fun AudioManager.supRequestAudioFocus() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val audioFocusRequest =
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                        .build()

        requestAudioFocus(audioFocusRequest)
    }
    else {
        requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
    }
}

fun AudioManager.supAnandonAudioFocus() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val audioFocusRequest =
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                        .build()

        abandonAudioFocusRequest(audioFocusRequest)
    }
    else {
        abandonAudioFocus(null)
    }
}