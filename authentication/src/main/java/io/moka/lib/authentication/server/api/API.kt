package io.moka.lib.authentication.server.api

import io.moka.lib.authentication.server.req.FindPasswordReq
import io.moka.lib.authentication.server.req.SignInUpReq
import io.moka.lib.authentication.server.res.BaseRes
import io.moka.lib.authentication.server.res.SignInUpRes
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

internal interface API {

    @POST("/dev/auth/sign_up")
    fun signUp(@Body signInUpReq: SignInUpReq): Call<SignInUpRes>

    @POST("/dev/auth/sign_in")
    fun signIn(@Body signInReq: SignInUpReq): Call<SignInUpRes>

    @POST("/dev/auth/find_password")
    fun findPassword(@Body findPasswordReq: FindPasswordReq): Call<BaseRes>

}
