package io.moka.authentication.server.res


class SignInUpRes : BaseRes() {
    var token: String? = null
    var user: User? = null

    class User {
        var id: String? = null
        var email: String? = null
        var nickname: String? = null
        var profile_image: String? = null
    }

}