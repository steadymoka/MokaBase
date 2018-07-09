package io.moka.lib.adhelper

import android.content.Context
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.facebook.ads.NativeAd
import com.facebook.ads.NativeAdBase

object AdCenter {

    /**
     * MUST SET BEFORE LOAD
     *
     * facebook Key , admob Key
     */

    val audienceNativeAdHash: HashMap<Int, NativeAd> = hashMapOf()
    val audienceBannerAdHash: HashMap<Int, AdView> = hashMapOf()

    val admobAdHash: HashMap<Int, com.google.android.gms.ads.AdView> = hashMapOf()

    /*
    Facebook audience
     */

    fun loadFbAudienceNativeAd(context: Context, position: Int, key: String) {
        if (null == audienceNativeAdHash[position]) {
            audienceNativeAdHash[position] = NativeAd(context, key)
        }

        audienceNativeAdHash[position]!!.loadAd(NativeAdBase.MediaCacheFlag.ALL)
    }

    fun makeFbAudienceBannerAd(context: Context, position: Int, key: String) {
        if (null == audienceBannerAdHash[position]) {
            audienceBannerAdHash[position] = AdView(context, key, AdSize.BANNER_HEIGHT_50)
        }
    }

    /*
    Google admob
     */

    fun makeAdmobAd(context: Context, position: Int, key: String) {
        if (null == admobAdHash[position]) {
            admobAdHash[position] = com.google.android.gms.ads.AdView(context).apply {
                adSize = com.google.android.gms.ads.AdSize.SMART_BANNER
                adUnitId = key
            }
        }
    }

}