package io.moka.lib.adhelper

import android.content.Context
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.google.android.gms.ads.AdRequest
import io.moka.lib.base.MokaBase
import io.moka.lib.base.util.MediaUtil
import io.moka.lib.base.util.log.MLog

/**
 *
 * About 전면 광고
 * Firebase Admob, Facebook Audience
 *
 */
class MokaInterstitialAd private constructor() {

    companion object {

        fun showInterstitialAd_admobFirst(context: Context, admobKey: String, facebookKey: String) {
            show_admob_InterstitialAd(context, admobKey, {}, {
                show_facebook_InterstitialAd(context, facebookKey, {}, {})
            })
        }

        fun showInterstitialAd_facebookFirst(context: Context, admobKey: String, facebookKey: String) {
            show_facebook_InterstitialAd(context, facebookKey, {}, {
                show_admob_InterstitialAd(context, admobKey, {}, {})
            })
        }

        /*
         */

        private fun show_admob_InterstitialAd(context: Context, key: String, success: () -> Unit, fail: () -> Unit) {
            val mInterstitialAd = com.google.android.gms.ads.InterstitialAd(context)
            mInterstitialAd.adUnitId = key
            mInterstitialAd.adListener = object : com.google.android.gms.ads.AdListener() {
                override fun onAdFailedToLoad(p0: Int) {
                    super.onAdFailedToLoad(p0)

                    MLog.deb("show_admob_InterstitialAd fail")
                    fail()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()

                    MLog.deb("show_admob_InterstitialAd success")
                    if (mInterstitialAd.isLoaded) {
                        MediaUtil.muteSound()
                        mInterstitialAd.show()
                    }
                    success()
                }

                override fun onAdClosed() {
                    super.onAdClosed()

                    MLog.deb("show_admob_InterstitialAd adClosed")
                    MediaUtil.unmuteSound()
                }
            }

            mInterstitialAd.loadAd(
                    AdRequest
                            .Builder()
                            .apply { if (MokaBase.debuggable) addTestDevice("B2E4DBF0638C3608DBC09D5FAE06DE10") }
                            .build())
        }

        private fun show_facebook_InterstitialAd(context: Context, key: String, success: () -> Unit, fail: () -> Unit) {
            val interstitialAd = InterstitialAd(context, key)
            interstitialAd.setAdListener(object : InterstitialAdListener {
                override fun onInterstitialDisplayed(p0: Ad?) {
                }

                override fun onAdClicked(p0: Ad?) {
                }

                override fun onInterstitialDismissed(p0: Ad?) {
                    MLog.deb("show_facebook_InterstitialAd dismissed")
                    MediaUtil.unmuteSound()
                }

                override fun onError(p0: Ad?, p1: AdError?) {
                    MLog.deb("show_facebook_InterstitialAd fail")
                    fail()
                }

                override fun onAdLoaded(p0: Ad?) {
                    MLog.deb("show_facebook_InterstitialAd success")
                    try {
                        if (interstitialAd.isAdLoaded) {
                            MediaUtil.muteSound()
                            interstitialAd.show()
                        }
                    } catch (e: Exception) {
                    }
                    success()
                }

                override fun onLoggingImpression(p0: Ad?) {
                }
            })
            interstitialAd.loadAd()
        }
    }

}