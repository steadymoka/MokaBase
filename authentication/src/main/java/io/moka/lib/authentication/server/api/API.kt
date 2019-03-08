package io.moka.lib.authentication.server.api

import io.moka.lib.authentication.server.ServerInfo
import io.moka.lib.authentication.server.req.FindPasswordReq
import io.moka.lib.authentication.server.req.SignInUpReq
import io.moka.lib.authentication.server.res.BaseRes
import io.moka.lib.authentication.server.res.SignInUpRes
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

internal interface API {

    @POST("/{prefix}/auth/sign_up")
    fun signUp(@Path(value = "prefix", encoded = true) pre: String = ServerInfo.prefix,
               @Body signUpReq: SignInUpReq): Call<SignInUpRes>

    @POST("/{prefix}/auth/sign_in")
    fun signIn(@Path(value = "prefix", encoded = true) pre: String = ServerInfo.prefix,
               @Body signInReq: SignInUpReq): Call<SignInUpRes>

    @POST("/{prefix}/auth/find_password")
    fun findPassword(@Path(value = "prefix", encoded = true) pre: String = ServerInfo.prefix,
                     @Body findPasswordReq: FindPasswordReq): Call<BaseRes>

}
