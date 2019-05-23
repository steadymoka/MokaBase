package io.moka.lib.audiopicker


import android.content.DialogInterface
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import io.moka.lib.base.util.FontSizeCompat
import io.moka.lib.base.util.color
import io.moka.lib.base.util.onClick
import kotlinx.android.synthetic.main.dialog_volume_picker.*
import kotlin.properties.Delegates


class MokaVolumePicker : AppCompatDialogFragment(), SeekBar.OnSeekBarChangeListener {

    private var listener: ((Float) -> Unit)? = null
    private var volume: Float = 0f
    private var title = ""
    private var steamType: Int = AudioManager.STREAM_MUSIC
    private var uri: Uri? = null

    private val blink by lazy { AnimationUtils.loadAnimation(activity!!, R.anim.blink) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dialog_volume_picker, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    override fun onResume() {
        super.onResume()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        AudioUtil.releasePlayer()
        super.onDestroyView()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        AudioUtil.stopPlayer()
    }

    fun resizeFontSizeAndExt() {
        FontSizeCompat.size(11f, textView_title, textView_cancel, textView_ok)
        FontSizeCompat.size(10f, textView_des)
    }

    /**
     */

    private fun initView() {
        if (title.isNotEmpty())
            textView_title.text = title

        /*
        알람 소리 크기 설정
         */
        seekBar_alarm_vol.progressDrawable.colorFilter = PorterDuffColorFilter(color(R.color.black_03_text), PorterDuff.Mode.SRC_IN)
        seekBar_alarm_vol.thumb.setColorFilter(color(R.color.black_03_text), PorterDuff.Mode.SRC_IN)
        seekBar_alarm_vol.max = AudioUtil.getMaxVol(steamType)
        seekBar_alarm_vol.progress = volume.toInt()
        seekBar_alarm_vol.setOnSeekBarChangeListener(this)

        /* 현재 볼륨 설정 */
        AudioUtil.setCurrentVol(volume.toInt(), steamType)

        /*
        set Event
         */
        textView_preListen.onClick { onClickPlayPause() }
        textView_ok.onClick { onClickOk() }
        textView_cancel.onClick { onClickCancel() }
    }

    /**
     * Listener
     */

    private var isPlaying by Delegates.observable(false) { _, _, value ->
        if (value) {
            imageView_playPause.setImageResource(R.drawable.vc_pause_black)
            imageView_playPause.startAnimation(blink)
        }
        else {
            imageView_playPause.setImageResource(R.drawable.vc_play_black)
            imageView_playPause.clearAnimation()
        }
    }

    private fun onClickPlayPause() {
        if (isPlaying) {
            AudioUtil.stopPlayer()
        }
        else {
            if (null == uri) {
                val audioUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                AudioUtil.playPlayer(audioUri, steamType)
            }
            else {
                AudioUtil.playPlayer(uri!!, steamType)
            }
        }
        isPlaying = !isPlaying
    }

    private fun onClickOk() {
        listener?.invoke(seekBar_alarm_vol.progress.toFloat())
        dismiss()
    }

    private fun onClickCancel() {
        dismiss()
    }

    /**
     */

    override fun onProgressChanged(seekBar: SeekBar?, value: Int, fromUser: Boolean) {
        when (seekBar?.id) {
            R.id.seekBar_alarm_vol -> AudioUtil.setCurrentVol(value, steamType)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    /**
     */

    fun setVolume(volume: Float): MokaVolumePicker {
        this.volume = volume
        return this
    }

    fun setTitle(title: String): MokaVolumePicker {
        this.title = title
        return this
    }

    fun setStreamType(steamType: Int): MokaVolumePicker {
        this.steamType = steamType
        return this
    }

    fun setAudioUri(uri: Uri): MokaVolumePicker {
        this.uri = uri
        return this
    }

    fun showDialog(manager: FragmentManager, listener: (Float) -> Unit) {
        this.listener = listener
        try {
            val ft = manager.beginTransaction()
            ft.add(this, "MokaVolumePicker")
            ft.commit()
        } catch (e: IllegalStateException) {
        }
    }

}
