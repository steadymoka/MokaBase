package io.moka.lib.authentication.util

import com.google.gson.Gson
import io.moka.lib.authentication.server.res.BaseRes
import io.moka.lib.authentication.server.res.ErrorRes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal fun <T : BaseRes> Call<T>.on(
        success: ((response: T) -> Unit)? = null,
        fail: ((error: ErrorRes?, Throwable?) -> Unit)? = null,
        filter: (() -> Boolean)? = null) {

    this.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>?, response: Response<T>) {
            /*
            response.isSuccessful : true - code 200
            response.isSuccessful : false - code < 200 || code >= 300

            unauthenticated - code == 401
            clientError - code >= 400 && code < 500
            serverError - code >= 500 && code < 600

            [출처: https://github.com/square/retrofit/blob/master/retrofit/src/main/java/retrofit2/OkHttpCall.java]
             */
            if (false == filter?.invoke())
                return

            if (response.isSuccessful) {
                success?.invoke(response.body()!!)
            }
            else {
                fail?.invoke(response.errorBody()?.string()?.parseError(), null)
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            /*
            network error etc..
             */
            if (false == filter?.invoke())
                return

            fail?.invoke(null, t)
        }
    })
}

internal fun String.parseError(): ErrorRes {
    return try {
        Gson().fromJson(this, ErrorRes::class.java)
    } catch (err: Exception) {
        ErrorRes()
    }
}