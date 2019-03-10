package io.moka.lib.authentication

import android.app.Service
import android.content.Intent
import android.os.IBinder
import io.moka.lib.base.util.log.MLog

class AuthenticationService : Service() {

    private val authenticator: Authenticator by lazy { Authenticator(this) }

    override fun onCreate() {
        MLog.deb("onCreate is called")
        authenticator
    }

    override fun onBind(intent: Intent): IBinder? {
        return authenticator.iBinder
    }

}
