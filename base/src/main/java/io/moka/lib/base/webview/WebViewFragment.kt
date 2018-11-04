package io.moka.lib.base.webview


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import io.moka.lib.base.R
import io.moka.lib.base.util.statusBarSize
import io.moka.lib.base.util.topMargin
import kotlinx.android.synthetic.main.layout_web_view.*


class WebViewFragment : Fragment() {

    private lateinit var webView: WebView
    private var mUrl: String? = null

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.mUrl = arguments?.getString(KEY_URL, "")

        if (!this.mUrl!!.startsWith("http"))
            this.mUrl = "http://" + this.mUrl!!

        if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT)
            activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.layout_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        loadUrl(mUrl ?: "")

        val title = activity!!.intent.getStringExtra(WebViewActivity.KEY_TITLE)
        if (null != title)
            setTitle(title)
    }

    fun onBackPressed(): Boolean {
        return goBack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (null != frameLayout_webViewContainer)
            frameLayout_webViewContainer.removeView(webView)

        webView.removeAllViews()
        webView.destroy()
    }

    /**
     */

    private fun initView() {
        linearLayout_toolbar.topMargin(statusBarSize)

        webView = WebView(activity)
        webView.layoutParams = ViewGroup.LayoutParams(-1, -1)
        webView.webViewClient = getWebViewClient()
        webView.webChromeClient = getWebChromeClient()
        webView.setInitialScale(1)
        initWebSettings(webView.settings)

        frameLayout_webViewContainer.layoutParams = RelativeLayout.LayoutParams(-1, -1)
        frameLayout_webViewContainer.addView(webView)

        imageView_back.setOnClickListener { onClose() }
        imageView_refresh.setOnClickListener { onRefresh() }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("SetJavaScriptEnabled", "NewApi")
    private fun initWebSettings(webSettings: WebSettings) {

        webSettings.pluginState = WebSettings.PluginState.ON
        webSettings.useWideViewPort = true
        webSettings.defaultZoom = WebSettings.ZoomDensity.MEDIUM
        webSettings.builtInZoomControls = false
        webSettings.setSupportZoom(false)
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.allowFileAccess = true
        webSettings.domStorageEnabled = true
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.setAppCacheEnabled(true)

        if (Build.VERSION_CODES.HONEYCOMB <= Build.VERSION.SDK_INT)
            webSettings.displayZoomControls = false
    }

    fun getWebViewClient(): WebViewClient {
        return MocaWebViewClient()
    }

    fun getWebChromeClient(): WebChromeClient {
        return MocaWebChromeClient()
    }

    fun onRefresh() {
        webView.reload()
    }

    fun onClose() {
        activity!!.finish()
    }

    fun goBack(): Boolean {
        if (webView.canGoBack()) {
            webView.goBack()
            return false
        }
        else {
            return true
        }
    }

    fun loadUrl(url: String) {
        webView.loadUrl(url)
    }

    fun setTitle(title: String) {
        textView_webviewTitle.text = title
    }

    fun updateProgressBar(progress: Int) {
        if (100 > progress && View.GONE == progressBar_progress.visibility)
            progressBar_progress.visibility = View.VISIBLE
        else if (100 == progress)
            progressBar_progress.visibility = View.GONE

        progressBar_progress.progress = progress
    }

    fun startLoading() {
        progressBar_loading.visibility = View.VISIBLE
        imageView_refresh.visibility = View.INVISIBLE
    }

    fun finishLoading() {
        progressBar_loading.visibility = View.INVISIBLE
        imageView_refresh.visibility = View.VISIBLE
    }

    private inner class MocaWebChromeClient : WebChromeClient() {

        override fun onProgressChanged(webView: WebView, progress: Int) {
            updateProgressBar(progress)
        }
    }

    private inner class MocaWebViewClient : WebViewClient() {

        override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
            startLoading()
        }

        override fun onPageFinished(webView: WebView, url: String) {
            if (url.startsWith("market://details?id=")) {
                val intent: Intent
                intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)

                try {
                    this@WebViewFragment.startActivity(intent)
                } catch (localActivityNotFoundException: ActivityNotFoundException) {
                }
            }
            else {
                if (isAdded)
                    finishLoading()
            }
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            val uri = Uri.parse(url)
            val lastPathSegment = uri.lastPathSegment

            if (lastPathSegment != null && lastPathSegment.endsWith(".mp4")) {
                val intent = Intent("android.intent.action.VIEW")
                intent.setDataAndType(uri, "video/*")
                webView.context.startActivity(intent)

                return true
            }
            return super.shouldOverrideUrlLoading(webView, url)
        }

    }

    companion object {

        val KEY_URL = "WebViewFragment.KEY_URL"
    }

}