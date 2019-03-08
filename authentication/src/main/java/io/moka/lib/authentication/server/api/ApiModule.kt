package io.moka.lib.authentication.server.api

import io.moka.lib.authentication.BuildConfig
import io.moka.lib.authentication.server.ServerInfo
import io.moka.lib.base.MokaBase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


internal object ApiModule {

    val api: API by lazy { retrofit.create(API::class.java) }

    /**
     */

    private val client by lazy {
        OkHttpClient.Builder()
                .addInterceptor(initHttpLoggingInterceptor())
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
                .client(client
                        .newBuilder()
                        .addInterceptor { chain ->
                            val userAgent = System.getProperty("http.agent") + " dayday/${BuildConfig.VERSION_CODE}"

                            chain.proceed(
                                    chain.request()
                                            .newBuilder()
                                            .addHeader("Accept", "application/json")
                                            .addHeader("Content-Type", "application/json")
                                            .addHeader("User-Agent", userAgent)
                                            .build()
                            )
                        }
                        .build()
                )
                .baseUrl(ServerInfo.endPoint)
                .addConverterFactory(GsonConverterFactory.create())
                .build()!!
    }

    private fun initHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        if (MokaBase.DEBUG)
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        else
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        return interceptor
    }

}
