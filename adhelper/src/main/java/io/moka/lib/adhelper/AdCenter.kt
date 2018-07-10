package io.moka.lib.adhelper

import android.content.Context
import com.facebook.ads.*
import com.google.android.gms.ads.MobileAds
import com.tnkfactory.ad.TnkSession

data class AdItem(val position: Int, val key: String)

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

    fun loadFbAudienceNativeAd(context: Context, adItem: AdItem) {
        if (null == audienceNativeAdHash[adItem.position]) {
            audienceNativeAdHash[adItem.position] = NativeAd(context, adItem.key)
        }

        audienceNativeAdHash[adItem.position]!!.loadAd(NativeAdBase.MediaCacheFlag.ALL)
    }

    fun makeFbAudienceBannerAd(context: Context, adItem: AdItem) {
        if (null == audienceBannerAdHash[adItem.position]) {
            audienceBannerAdHash[adItem.position] = AdView(context, adItem.key, AdSize.BANNER_HEIGHT_50)
        }
    }

    /*
    Google admob
     */

    fun makeAdmobAd(context: Context, adItem: AdItem) {
        if (null == admobAdHash[adItem.position]) {
            admobAdHash[adItem.position] = com.google.android.gms.ads.AdView(context).apply {
                adSize = com.google.android.gms.ads.AdSize.SMART_BANNER
                adUnitId = adItem.key
            }
        }
    }

}