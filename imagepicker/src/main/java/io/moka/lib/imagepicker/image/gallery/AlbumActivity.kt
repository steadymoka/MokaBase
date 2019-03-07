package io.moka.lib.imagepicker.image.gallery


import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.moka.lib.imagepicker.R
import io.moka.lib.imagepicker.image.gallery.adapter.AlbumItemData
import kotlinx.android.synthetic.main.activity_album.*


class AlbumActivity : AppCompatActivity() {

    private var maxImageCount: Int = 0
    private var currentBucketName: String? = null
    private var oneAlbumFragment: OneAlbumFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        init()
    }

    private fun init() {
        maxImageCount = intent.getIntExtra(KEY_MAX_IMAGE_COUNT, 10)

        initFragmentStack()
        initToolbar()
    }

    private fun initFragmentStack() {
        val containerViewId = R.id.frameLayout_container
        val albumsFragment = AlbumsFragment()
        albumsFragment.onClickAlbum = { onAlbumClick(it) }

        supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(containerViewId, albumsFragment)
                .commitAllowingStateLoss()
    }

    private fun initToolbar() {
        textView_toolbarTitle.text = getString(R.string.title_albums)
        imageView_home.setImageResource(R.drawable.vc_x_gray)
        imageView_home.setPadding(dpToPx(18f), dpToPx(18f), dpToPx(18f), dpToPx(18f))
        textView_menu.visibility = View.GONE

        /**/
        imageView_home.setOnClickListener { onBackPressed() }
        textView_menu.setOnClickListener { oneAlbumFragment!!.onCompleteImageSelection() }
    }

    /**
     */

    private fun onAlbumClick(albumItem: AlbumItemData) {
        onClickAlbum(albumItem)
    }

    private fun onClickAlbum(albumItemData: AlbumItemData) {
        currentBucketName = albumItemData.bucketname

        imageView_home.setImageResource(R.drawable.vc_arrow_back)
        imageView_home.setPadding(dpToPx(21f), dpToPx(21f), dpToPx(21f), dpToPx(21f))

        if (maxImageCount == 1)
            textView_toolbarTitle.text = albumItemData.bucketname
        else
            textView_toolbarTitle.text = albumItemData.bucketname + " " + 0
        textView_menu.visibility = View.VISIBLE

        oneAlbumFragment = OneAlbumFragment()
        oneAlbumFragment!!.setAlbumData(albumItemData)
        oneAlbumFragment!!.setMaxImageCount(maxImageCount)
        oneAlbumFragment!!.setTitleListener(object : TitleListener {

            override fun setTitle(selectedImageCount: Int) {
                if (maxImageCount == 1)
                    textView_toolbarTitle.text = currentBucketName
                else
                    textView_toolbarTitle.text = currentBucketName + " " + selectedImageCount
            }

        })

        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right)
                .add(R.id.frameLayout_container, oneAlbumFragment!!)
                .addToBackStack(oneAlbumFragment.toString())
                .commitAllowingStateLoss()
    }

    /**
     */

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            imageView_home.setImageResource(R.drawable.vc_x_gray)
            imageView_home.setPadding(dpToPx(18f), dpToPx(18f), dpToPx(18f), dpToPx(18f))

            textView_toolbarTitle.text = getString(R.string.title_albums)
            textView_menu.visibility = View.GONE
        }

        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right)
    }

    /**
     */

    interface TitleListener {

        fun setTitle(selectedImageCount: Int)

    }

    companion object {
        const val KEY_MAX_IMAGE_COUNT = "AlbumActivity.KEY_MAX_IMAGE_COUNT"
        const val KEY_SELECTED_IMAGES = "GalleryActivity.KEY_SELECTED_IMAGES"
    }

    fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
    }

}
