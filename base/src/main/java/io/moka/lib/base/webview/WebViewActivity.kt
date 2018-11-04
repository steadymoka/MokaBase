package io.moka.lib.base.webview


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.moka.lib.base.R


class WebViewActivity : AppCompatActivity() {

    private var webViewFragment: WebViewFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_web_view_container)

        val intent = intent
        val url = intent.getStringExtra(KEY_URL)

        if (null != url) {
            webViewFragment = WebViewFragment()

            val args = Bundle()
            args.putString(WebViewFragment.KEY_URL, url)
            webViewFragment!!.arguments = args

            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frameLayout_webViewFragmentContainer, webViewFragment!!)
                    .commit()
        }
    }

    override fun onBackPressed() {
        if (webViewFragment!!.onBackPressed())
            super.onBackPressed()
    }

    companion object {

        const val KEY_URL = "WebViewActivity.KEY_URL"
        const val KEY_TITLE = "WebViewActivity.KEY_TITLE"

        fun goWebView(activity: Activity, url: String) {
            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra(WebViewActivity.KEY_URL, url)
            intent.putExtra(WebViewActivity.KEY_TITLE, "haruharu.io")
            activity.startActivity(intent)
        }

        fun goInsta(activity: Activity, url: String) {
            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra(WebViewActivity.KEY_URL, url)
            intent.putExtra(WebViewActivity.KEY_TITLE, "Instagram")
            activity.startActivity(intent)
        }
    }

}
