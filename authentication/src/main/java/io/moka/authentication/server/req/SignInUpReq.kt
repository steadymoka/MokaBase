package io.moka.authentication.server.req


data class SignInUpReq(

        var email: String,
        var password: String,
        var nickname: String? = null,
        var signup_from: Int = 2

)