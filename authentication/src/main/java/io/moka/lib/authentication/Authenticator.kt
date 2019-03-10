package io.moka.lib.authentication

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import io.moka.lib.authentication.server.api.ApiModule
import io.moka.lib.authentication.server.req.SignInUpReq
import io.moka.lib.base.util.log.MLog


class Authenticator(private var context: Context) : AbstractAccountAuthenticator(context) {

    override fun getAuthTokenLabel(p0: String?): String {
        return ""
    }

    override fun confirmCredentials(p0: AccountAuthenticatorResponse?, p1: Account?, p2: Bundle?): Bundle {
        return Bundle()
    }

    override fun updateCredentials(p0: AccountAuthenticatorResponse?, p1: Account?, p2: String?, p3: Bundle?): Bundle {
        return Bundle()
    }

    override fun hasFeatures(p0: AccountAuthenticatorResponse?, p1: Account?, p2: Array<out String>?): Bundle {
        return Bundle()
    }

    override fun editProperties(p0: AccountAuthenticatorResponse?, p1: String?): Bundle {
        return Bundle()
    }

    override fun getAuthToken(response: AccountAuthenticatorResponse?, account: Account, authTokenType: String?, options: Bundle?): Bundle? {
        val accountManager = AccountManager.get(context)

        var authToken = accountManager.peekAuthToken(account, authTokenType)
        Log.wtf("DayDay", "> peekAuthToken returned - $authToken")

        // Lets give another try to authenticate the user
        if (authToken.isNullOrEmpty()) {
            val password = accountManager.getPassword(account)
            val email = accountManager.getUserData(account, "email")

            if (password != null) {
                try {
                    Log.d("DayDay", "> re-authenticating with the existing password")
                    val signInReq = SignInUpReq(email, password)
                    val res = ApiModule.api.signIn(signInReq = signInReq).execute()
                    if (res.isSuccessful) {
                        authToken = res.body()!!.token
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // If we get an authToken - we return it
        if (!authToken.isNullOrEmpty()) {
            val result = Bundle()
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
            return result
        }

        return null
    }

    /**
     *
     * accountType - AuthContract.ACCOUNT_TYPE (io.moka.dayday)
     * authTokenType - Alarm | Pomodoro | Diary ... in AuthContract.kt
     */
    override fun addAccount(response: AccountAuthenticatorResponse?, accountType: String?, authTokenType: String?, requiredFeatures: Array<out String>?, options: Bundle?): Bundle {
        MLog.deb("Authenticator > accountType : ${accountType}     authTokenType : ${authTokenType}")
        val intent = Intent(context, AccountIntroLayout::class.java)
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType)
        intent.putExtra(AccountManager.KEY_AUTH_TOKEN_LABEL, authTokenType)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)

        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

}