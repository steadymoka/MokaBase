package io.moka.authentication

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AuthenticationService : Service() {

    private val authenticator: Authenticator by lazy { Authenticator(this) }

    override fun onCreate() {
        authenticator
    }

    override fun onBind(intent: Intent): IBinder? {
        return authenticator.iBinder
    }

}
