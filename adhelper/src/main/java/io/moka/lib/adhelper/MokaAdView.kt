package io.moka.lib.adhelper


import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.facebook.ads.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.NativeExpressAdView
import com.tnkfactory.ad.NativeAdItem
import com.tnkfactory.ad.NativeAdListener
import com.tnkfactory.ad.TnkSession
import io.moka.lib.MokaBase
import io.moka.lib.util.FontSizeUtil
import io.moka.lib.util.dp2px
import io.moka.lib.util.log.MLog
import kotlinx.android.synthetic.main.view_moka_ad_banner.view.*
import kotlinx.android.synthetic.main.view_moka_ad_native.view.*

enum class AdType {

    NATIVE, BANNER

}

enum class Period {

    FACEBOOK_ADMOB_TNK,
    FACEBOOK_TNK_ADMOB,
    ADMOB_FACEBOOK_TNK,
    FACEBOOK_ADMOB,
    ADMOB_FACEBOOK

}

class MokaAdView constructor(context: Context, attrs: AttributeSet? = null,
                             adType: AdType,
                             nativeResId: Int = R.layout.view_moka_ad_native,
                             private var period: Period = Period.FACEBOOK_ADMOB_TNK)

    : FrameLayout(context, attrs) {

    init {
        when (adType) {
            AdType.NATIVE -> {
                View.inflate(context, nativeResId, this)
                (frameLayout_media.layoutParams as RelativeLayout.LayoutParams).height = dp2px(1.0).toInt()
            }
            AdType.BANNER -> {
                View.inflate(context, R.layout.view_moka_ad_banner, this)
            }
        }
    }

    /* **



    ** */

    private var callback: ((isSuccess: Boolean) -> Unit)? = null
    private var filter: (() -> Boolean)? = null

    private var position: Int = -1
    private var isMedia: Boolean = true

    private var admobNativeAdView: NativeExpressAdView? = null
    private var admobBannerAdView: AdView? = null

    private fun initView() {
        FontSizeUtil.size(13f, native_ad_title)
        FontSizeUtil.size(12f, textView_native_noFill, textView_banner_noFill)
        FontSizeUtil.size(10f, native_ad_social_context)
    }

    /**
     * 실행 함수
     */

    fun showBanner(position: Int, filter: (() -> Boolean)? = null, callback: ((isSuccess: Boolean) -> Unit)? = null) {
        this.position = position
        this.filter = filter
        this.callback = callback

        showAdBanner()
    }

    fun showNative(position: Int, isMedia: Boolean, filter: (() -> Boolean)? = null, callback: ((isSuccess: Boolean) -> Unit)? = null) {
        this.position = position
        this.isMedia = isMedia
        this.filter = filter
        this.callback = callback

        showAdNative()
    }

    /**                                            ****
     *                                              ***
     *            *      ****   *   *  ****          **
     **           *     *    *   * *   ****           *
     ***          ****   ****     *    ****           *
     ****                                            **/

    private fun showAdNative() {
        cardView_native.visibility = View.VISIBLE
        frameLayout_loading_native.visibility = View.VISIBLE

        when (period) {
            Period.FACEBOOK_TNK_ADMOB -> {
                load_facebook_nativeAd {
                    load_tnk_nativeAd {
                        load_admob_nativeAd {
                            showError_nativeAd()
                            callback?.invoke(false)
                        }
                    }
                }
            }

            Period.ADMOB_FACEBOOK_TNK -> {
                load_admob_nativeAd {
                    load_facebook_nativeAd {
                        load_tnk_nativeAd {
                            showError_nativeAd()
                            callback?.invoke(false)
                        }
                    }
                }
            }

            else -> {

            }
        }
    }

    private fun showAdBanner() {
        when (period) {
            Period.FACEBOOK_ADMOB -> {
                load_facebook_bannerAd {
                    load_admob_bannerAd {
                        showError_bannerAd()
                        callback?.invoke(false)
                    }
                }
            }

            Period.ADMOB_FACEBOOK -> {
                load_admob_bannerAd {
                    load_facebook_bannerAd {
                        showError_bannerAd()
                        callback?.invoke(false)
                    }
                }
            }

            else -> {
            }
        }
    }

    fun onDestroy(position: Int) {
        AdCenter.audienceNativeAdHash[position]?.destroy()
        AdCenter.audienceNativeAdHash[position]?.unregisterView()
        AdCenter.audienceBannerAdHash[position]?.destroy()
        admobNativeAdView?.destroy()
        admobBannerAdView?.destroy()
    }

    /**                                            ****
     *                                              ***
     *            *      ****   *   *  ****          **
     **           *     *    *   * *   ****           *
     ***          ****   ****     *    ****           *
     ****                                            **/

    fun showError_bannerAd() {
        banner_container.visibility = View.GONE
        textView_banner_noFill.visibility = View.VISIBLE
    }

    fun showError_nativeAd() {
        (frameLayout_media.layoutParams as RelativeLayout.LayoutParams).height = dp2px(1.0).toInt()
        frameLayout_loading_native.visibility = View.GONE
        cardView_native.visibility = View.GONE

        textView_native_noFill.visibility = View.VISIBLE
    }

    /**
     *
     *
     *
     *
     *
     * facebook audience network
     */

    private fun load_facebook_nativeAd(fail: () -> Unit) {
        val facebookNativeAd = AdCenter.audienceNativeAdHash[position] ?: return

        if (facebookNativeAd.isAdLoaded) {
            inflateNativeAdViews(facebookNativeAd)
        }
        else {
            facebookNativeAd.setAdListener(object : com.facebook.ads.NativeAdListener {
                override fun onMediaDownloaded(p0: Ad?) {
                }

                override fun onAdLoaded(ad: Ad) {
                    if (filter?.invoke() == false)
                        return
                    if (ad != facebookNativeAd)
                        return

                    MLog.deb("facebook onAdLoaded :")
                    inflateNativeAdViews(facebookNativeAd)
                    callback?.invoke(true)
                }

                override fun onError(ad: Ad?, error: AdError?) {
                    if (filter?.invoke() == false)
                        return

                    MLog.deb("facebook error : ${error?.errorCode} / ${error?.errorMessage}")
                    fail()
                }

                override fun onAdClicked(p0: Ad?) {
                }

                override fun onLoggingImpression(p0: Ad?) {
                }

            })
            facebookNativeAd.loadAd(NativeAdBase.MediaCacheFlag.ALL)
        }
    }

    private fun inflateNativeAdViews(facebookNativeAd: NativeAd) {
        // layout setting
        ad_native_inner_container.visibility = View.VISIBLE
        frameLayout_loading_native.visibility = View.GONE

        if (isMedia) {
            (frameLayout_media.layoutParams as RelativeLayout.LayoutParams).height = dp2px(150.0).toInt()
            native_ad_media.visibility = View.VISIBLE
            imageView_image_ad.visibility = View.GONE
        }
        else {
            (frameLayout_media.layoutParams as RelativeLayout.LayoutParams).height = dp2px(1.0).toInt()
            native_ad_media.visibility = View.GONE
            imageView_image_ad.visibility = View.GONE
        }

        (native_ad_icon.layoutParams as RelativeLayout.LayoutParams).topMargin = dp2px(6.0).toInt()

        // Setting the Text.
        native_ad_title.text = facebookNativeAd.advertiserName
        native_ad_social_context.text = facebookNativeAd.adSocialContext
        native_ad_body.text = facebookNativeAd.adBodyText
        native_ad_call_to_action.text = facebookNativeAd.adCallToAction

        val adChoicesView = AdChoicesView(context, facebookNativeAd, true)
        adChoicesView.setPadding(0, 7, 0, 7)

        ad_native_inner_container.addView(adChoicesView, 0)

        // Register the Title and CTA button to listen for clicks.
        val clickableViews: ArrayList<View> = ArrayList()
        clickableViews.add(native_ad_media)
        clickableViews.add(native_ad_title)
        clickableViews.add(native_ad_call_to_action)

        facebookNativeAd.registerViewForInteraction(ad_native_inner_container, native_ad_media, native_ad_icon, clickableViews)
    }

    private fun load_facebook_bannerAd(fail: () -> Unit) {
        val facebookBannerAd = AdCenter.audienceBannerAdHash[position] ?: return

        banner_container.addView(facebookBannerAd)

        facebookBannerAd.setAdListener(object : AdListener {
            override fun onAdLoaded(ad: Ad) {
                if (filter?.invoke() == false)
                    return
                callback?.invoke(true)
            }

            override fun onError(ad: Ad?, error: AdError?) {
                if (filter?.invoke() == false)
                    return
                banner_container.removeAllViews()
                fail()
            }

            override fun onAdClicked(p0: Ad?) {
            }

            override fun onLoggingImpression(p0: Ad?) {
            }
        })
        facebookBannerAd.loadAd()
    }

    /**
     *
     *
     *
     *
     *
     *
     * About Admob
     */

    var adWidth: Int = 340
    var adHeight: Int = 250

    private fun load_admob_nativeAd(fail: () -> Unit) {
        val admobBannerAdView = AdCenter.admobAdHash[position] ?: return

        admobBannerAdView.adListener = object : com.google.android.gms.ads.AdListener() {

            override fun onAdLoaded() {
                super.onAdLoaded()
                MLog.deb("admob onAdLoaded :")

                frameLayout_loading_native.visibility = View.GONE
                textView_native_noFill.visibility = View.GONE

                ad_native_inner_container.visibility = View.VISIBLE
                cardView_native.visibility = View.VISIBLE

                cardView_native.removeView(admobNativeAdView)
                cardView_native.addView(admobNativeAdView)
                callback?.invoke(true)
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                MLog.deb("admob fail errorCode : $errorCode")
                fail()
            }
        }
        admobBannerAdView.loadAd(
                AdRequest
                        .Builder()
                        .apply { if (MokaBase.debuggable) addTestDevice(Contract.textDeviceCode) }
                        .build()
        )
    }

    private fun load_admob_bannerAd(fail: () -> Unit) {
        val admobBannerAdView = AdCenter.admobAdHash[position] ?: return

        admobBannerAdView.adListener = object : com.google.android.gms.ads.AdListener() {

            override fun onAdLoaded() {
                super.onAdLoaded()
                if (filter?.invoke() == false)
                    return
                callback?.invoke(true)
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                if (filter?.invoke() == false)
                    return
                banner_container.removeAllViews()
                fail()
            }
        }
        banner_container.addView(admobBannerAdView)

        admobBannerAdView.loadAd(
                AdRequest
                        .Builder()
                        .apply { if (MokaBase.debuggable) addTestDevice(Contract.textDeviceCode) }
                        .build()
        )
    }

    /**
     *
     *
     *
     *
     * tnk ad
     */

    var adItemTnk: NativeAdItem? = null

    fun load_tnk_nativeAd(fail: () -> Unit) {
        TnkSession.prepareNativeAd(context, TnkSession.CPC, NativeAdItem.STYLE_LANDSCAPE or NativeAdItem.STYLE_ICON, object : NativeAdListener {

            override fun onLoad(adItem: NativeAdItem) {
                MLog.deb("tnk onLoad is called")

                ad_native_inner_container.visibility = View.VISIBLE
                frameLayout_loading_native.visibility = View.GONE
                cardView_native.visibility = View.VISIBLE
                textView_native_noFill.visibility = View.GONE

                adItemTnk = adItem

                native_ad_title.text = adItem.title
                native_ad_social_context.text = adItem.description

                if (null != adItem.iconImage)
                    native_ad_icon.setImageBitmap(adItem.iconImage)

                if (isMedia) {
                    if (null != adItem.coverImage) {
                        (frameLayout_media.layoutParams as RelativeLayout.LayoutParams).height = dp2px(180.0).toInt()
                        (native_ad_icon.layoutParams as RelativeLayout.LayoutParams).topMargin = dp2px(6.0).toInt()

                        imageView_image_ad.setImageBitmap(adItem.coverImage)
                    }
                    else {
                        (frameLayout_media.layoutParams as RelativeLayout.LayoutParams).height = dp2px(1.0).toInt()
                    }
                }
                else {
                    (frameLayout_media.layoutParams as RelativeLayout.LayoutParams).height = dp2px(1.0).toInt()
                }

                (textView_sponsor_please.layoutParams as RelativeLayout.LayoutParams).leftMargin = dp2px(8.0).toInt()
                adItem.attachLayout(ad_native_inner_container)

                callback?.invoke(true)
            }

            override fun onFailure(errCode: Int) {
                MLog.deb("tnk onFailure is called : $errCode")
                fail()
            }

            override fun onClick() {}

            override fun onShow() {}
        })
    }

}