package io.moka.lib.audiopicker


import android.content.DialogInterface
import android.database.Cursor
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.jakewharton.rxbinding2.widget.RxTextView
import io.moka.lib.base.MokaBase
import io.moka.lib.base.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.dialog_audio_picker.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.util.*
import java.util.concurrent.TimeUnit


class MokaAudioPicker : AppCompatDialogFragment(), LoaderManager.LoaderCallbacks<Cursor>, SeekBar.OnSeekBarChangeListener {

    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    private var onButtonListener: ((Float, String, String) -> Unit)? = null

    private val defaultSongsAdapter by lazy { AudioAdapter(activity!!) }
    private val mySongsAdapter by lazy { AudioAdapter(activity!!) }

    private var defaultSongs: HashMap<String, String>? = null
    private var mySongs: HashMap<String, String>? = null

    private var defaultSongDataList: ArrayList<AudioAdapter.Data> = ArrayList()
    private var mySongDataList: ArrayList<AudioAdapter.Data> = ArrayList()

    private var searchSongs: ArrayList<AudioAdapter.Data> = ArrayList()

    private var volume: Float = 0f

    private var isSound = true
    private var selectedAudioTitle = ""
    private var indexSongList = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dialog_audio_picker, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAlarmList()

        initView()
        bindEvent()
    }

    override fun onResume() {
        super.onResume()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        AudioUtil.releasePlayer()
        super.onDestroyView()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        AudioUtil.stopPlayer()
    }

    fun resizeFontSizeAndExt() {
        FontSizeCompat.size(11f, textView_cancel, textView_ok)
        FontSizeCompat.size(12f, editText_search)
        FontSizeCompat.size(11f, textView_default, textView_mine)
        FontSizeCompat.size(10f, label_preListen, label_alarm_volume)
    }

    /**
     */

    private fun setAlarmList() {
        defaultSongs = HashMap()
        defaultSongs!![activity!!.getString(R.string.basic_audio)] = Uri.parse("android.resource://${MokaBase.context!!.packageName}/${R.raw.default_audio}").toString()

        val manager = RingtoneManager(activity)
        manager.setType(RingtoneManager.TYPE_RINGTONE)

        try {
            val cursor = manager.cursor
            if (0 < cursor!!.count) {
                while (cursor.moveToNext()) {
                    val notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                    val notificationUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX)
                    defaultSongs!![notificationTitle] = notificationUri
                }
            }
        } catch (ignore: Exception) {
            ignore.printStackTrace()
        } finally {
        }
    }

    private fun initView() {
        LoaderManager.getInstance(this).initLoader(0, null, this)

        /*
        소리 크기 설정
         */
        seekBar_preListen_vol.max = AudioUtil.getMaxVol(AudioManager.STREAM_MUSIC)
        seekBar_preListen_vol.progress = AudioUtil.getCurrentVol(AudioManager.STREAM_MUSIC)
        seekBar_preListen_vol.setOnSeekBarChangeListener(this)

        /*
        실제 알람 소리 크기 설정
         */
        seekBar_alarm_vol.progressDrawable.colorFilter = PorterDuffColorFilter(color(R.color.black_03_text), PorterDuff.Mode.SRC_IN)
        seekBar_alarm_vol.thumb.setColorFilter(color(R.color.black_03_text), PorterDuff.Mode.SRC_IN)
        seekBar_alarm_vol.max = AudioUtil.getMaxVol(AudioManager.STREAM_ALARM)
        seekBar_alarm_vol.progress = volume.toInt()
        seekBar_alarm_vol.setOnSeekBarChangeListener(this)

        /*
        recyclerView 초기화
        */
        recyclerView_default.init(activity!!, defaultSongsAdapter)
        defaultSongsAdapter.items = ArrayList()

        recyclerView_mine.init(activity!!, mySongsAdapter)
        mySongsAdapter.items = ArrayList()

        val height = dp2px((360 + 42).toDouble())
        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height.toInt())
        layoutParams.addRule(RelativeLayout.BELOW, textView_default.id)
        recyclerView_default.layoutParams = layoutParams
        recyclerView_mine.layoutParams = layoutParams

        /* ios style ui feedback */
        OverScrollDecoratorHelper.setUpOverScroll(recyclerView_default, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
        OverScrollDecoratorHelper.setUpOverScroll(recyclerView_mine, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)

        initDefaultSongsAdapter()
    }

    private fun bindEvent() {
        textView_default.setOnClickListener { onClickDefaultMusic() }
        textView_mine.setOnClickListener { onClickMineMusic() }
        textView_ok.setOnClickListener { onClickOk() }
        textView_cancel.setOnClickListener { onClickCancel() }
        imageView_sound.setOnClickListener { onClickSound() }

        defaultSongsAdapter.onClickItem = {}
        defaultSongsAdapter.onClickPlayController = {
            hideSoftKey(editText_search, activity!!)
            AudioUtil.stopPlayer()
            if (it.isPlaying) {
                if (isSound) {
                    AudioUtil.playPlayer(defaultSongs!![it.title]
                            ?: "", AudioManager.STREAM_MUSIC)
                }
            }
        }

        mySongsAdapter.onClickItem = {}
        mySongsAdapter.onClickPlayController = {
            hideSoftKey(editText_search, activity!!)
            AudioUtil.stopPlayer()
            if (it.isPlaying) {
                if (isSound) {
                    AudioUtil.playPlayer(mySongs!![it.title]
                            ?: "", AudioManager.STREAM_MUSIC)
                }
            }
        }

        relativeLayout_container.setOnTouchListener { _, _ ->
            hideSoftKey(editText_search, activity!!)
            true
        }

        onClickDefaultMusic()
    }

    private fun initDefaultSongsAdapter() {
        if (null == defaultSongs)
            return

        var selectedIndex = 0
        defaultSongDataList = ArrayList()

        for ((i, songTitle) in defaultSongs!!.keys.withIndex()) {
            val stringItemData = AudioAdapter.Data(songTitle)
            if (songTitle == selectedAudioTitle) {
                selectedIndex = i
                defaultSongsAdapter.selectedItem = stringItemData
            }

            defaultSongDataList.add(stringItemData)
        }

        // 동기화로 인해 내장 알람소리중에 없는 이름이 들어올수 있다.
        if (null == defaultSongsAdapter.selectedItem && defaultSongDataList.size > 0)
            defaultSongsAdapter.selectedItem = defaultSongDataList[0]

        defaultSongsAdapter.items = defaultSongDataList

        if (0 != selectedIndex)
            recyclerView_default.scrollToPosition(selectedIndex)
    }

    private fun initMySongsAdapter() {
        if (null == mySongs)
            return

        var selectedIndex = 0

        for ((i, songTitle) in mySongs!!.keys.withIndex()) {
            val stringItemData = AudioAdapter.Data(songTitle)
            if (songTitle == selectedAudioTitle) {
                selectedIndex = i
                mySongsAdapter.selectedItem = stringItemData
                onClickMineMusic()
            }

            mySongDataList.add(stringItemData)
        }

        // 동기화로 인해 내장 알람소리중에 없는 이름이 들어올수 있다.
        if (null == mySongsAdapter.selectedItem && mySongDataList.size > 0)
            mySongsAdapter.selectedItem = mySongDataList[0]

        mySongsAdapter.items = mySongDataList

        if (0 != selectedIndex)
            recyclerView_mine.scrollToPosition(selectedIndex)
    }

    /**
     * Listener
     */

    private fun setOnEditTextEvent() {
        compositeDisposable.add(
                RxTextView.textChanges(editText_search)
                        .debounce(222.toLong(), TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ text ->
                            if (text.isNullOrEmpty())
                                mySongsAdapter.items = mySongDataList

                            searchSongs.clear()
                            mySongDataList
                                    .filter {
                                        it.title.toLowerCase().contains(text.toString().toLowerCase())
                                    }
                                    .forEach {
                                        searchSongs.add(it)
                                    }

                            mySongsAdapter.items = searchSongs
                        }, { it.printStackTrace() }, {})
        )
    }

    private fun onClickDefaultMusic() {
        AudioUtil.stopPlayer()
        mySongsAdapter.playingItem = null
        mySongsAdapter.notifyDataSetChanged()
        hideSoftKey(editText_search, activity!!)

        indexSongList = 0
        textView_default.setTextColor(color(R.color.black_03_text))
        textView_mine.setTextColor(color(R.color.black_06_sub_text))
        view_default_line.visibility = View.VISIBLE
        view_mine_line.visibility = View.INVISIBLE
        recyclerView_default.visibility = View.VISIBLE
        recyclerView_mine.visibility = View.INVISIBLE
        relativeLayout_search.visibility = View.GONE
    }

    private fun onClickMineMusic() {
        AudioUtil.stopPlayer()
        defaultSongsAdapter.playingItem = null
        defaultSongsAdapter.notifyDataSetChanged()
        hideSoftKey(editText_search, activity!!)

        indexSongList = 1
        textView_default.setTextColor(color(R.color.black_06_sub_text))
        textView_mine.setTextColor(color(R.color.black_03_text))
        view_default_line.visibility = View.INVISIBLE
        view_mine_line.visibility = View.VISIBLE
        recyclerView_default.visibility = View.INVISIBLE
        recyclerView_mine.visibility = View.VISIBLE
        relativeLayout_search.visibility = View.VISIBLE
    }

    private fun onClickSound() {
        if (isSound) {
            AudioUtil.stopPlayer()
            imageView_sound.setImageResource(R.drawable.vc_sound_mute_white)
        }
        else {
            imageView_sound.setImageResource(R.drawable.vc_sound_white)
        }

        isSound = !isSound
    }

    private fun onClickOk() {
        if (null != onButtonListener) {
            if (indexSongList == 0) {
                val songTitle = defaultSongsAdapter.selectedItem!!.title
                onButtonListener!!(seekBar_alarm_vol.progress.toFloat(), songTitle, defaultSongs!![songTitle]!!)
            }
            else {
                if (null == mySongsAdapter.selectedItem) {
                    Toast.makeText(activity, R.string.toast_select_alarm_song, Toast.LENGTH_LONG).show()
                    return
                }
                val songTitle = mySongsAdapter.selectedItem!!.title
                onButtonListener!!(seekBar_alarm_vol.progress.toFloat(), songTitle, mySongs!![songTitle]!!)
            }
        }
        dismiss()
    }

    private fun onClickCancel() {
        dismiss()
    }

    /**
     */

    override fun onProgressChanged(seekBar: SeekBar?, value: Int, fromUser: Boolean) {
        when (seekBar?.id) {
            R.id.seekBar_preListen_vol -> AudioUtil.setCurrentVol(value, AudioManager.STREAM_MUSIC)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        if (null == cursor)
            return

        val songs = HashMap<String, String>()

        if (0 < cursor.count)
            while (cursor.moveToNext()) {
                val notificationTitle = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val notificationUri = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))

                songs[notificationTitle] = notificationUri
            }
        this.mySongs = songs
        initMySongsAdapter()
        setOnEditTextEvent()
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(
                activity!!,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA),
                null, null, null)
    }

    /**
     */

    fun setAudioTitle(selectedAudioTitle: String = ""): MokaAudioPicker {
        this.selectedAudioTitle = selectedAudioTitle
        return this
    }

    fun setSectableColor(selectableColor: Int): MokaAudioPicker {
        mySongsAdapter.selectableColor = selectableColor
        defaultSongsAdapter.selectableColor = selectableColor
        return this
    }

    fun showDialog(manager: FragmentManager, onDialogButtonClickListener: (Float, String, String) -> Unit) {
        this.onButtonListener = onDialogButtonClickListener
        try {
            val ft = manager.beginTransaction()
            ft.add(this, "MokaAudioPicker")
            ft.commit()
        } catch (e: IllegalStateException) {
        }
    }

}
