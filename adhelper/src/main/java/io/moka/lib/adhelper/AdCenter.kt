package io.moka.lib.adhelper

import android.content.Context
import com.facebook.ads.*
import com.google.android.gms.ads.MobileAds
import com.tnkfactory.ad.TnkSession

object AdCenter {

    fun initialize(context: Context, admobAppKey: String) {
        MobileAds.initialize(context, admobAppKey)
        TnkSession.initInstance(context)
    }

    fun setAudienceTestDevice(testDeviceCode: String) {
        AdSettings.addTestDevice(testDeviceCode)
    }

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