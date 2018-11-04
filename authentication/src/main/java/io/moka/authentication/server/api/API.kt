package io.moka.authentication.server.api

import io.moka.authentication.server.req.FindPasswordReq
import io.moka.authentication.server.req.SignInReq
import io.moka.authentication.server.req.SignInUpReq
import io.moka.authentication.server.res.BaseRes
import io.moka.authentication.server.res.SignInUpRes
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface API {

    @POST("/dev/auth/sign_up")
    fun signUp(@Body signInUpReq: SignInUpReq): Call<SignInUpRes>

    @POST("/dev/auth/sign_in")
    fun signIn(@Body signInReq: SignInUpReq): Call<SignInUpRes>

    @POST("/dev/auth/find_password")
    fun findPassword(@Body findPasswordReq: FindPasswordReq): Call<BaseRes>

}
