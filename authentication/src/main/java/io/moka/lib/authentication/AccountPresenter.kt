package io.moka.lib.authentication

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import io.moka.lib.authentication.server.api.ApiModule
import io.moka.lib.authentication.server.req.SignInUpReq
import io.moka.lib.authentication.util.Contract
import io.moka.lib.authentication.util.on
import io.moka.lib.base.util.log.MLog
import org.jetbrains.anko.support.v4.toast
import java.net.SocketTimeoutException


class AccountPresenter(private val view: AccountLayout) {

    private val viewModel: AccountLayout.ViewModel = view.viewModel

    /**
     */

    fun reqSignUp() {
        if (viewModel.password.length < 8 || viewModel.password.length > 20) {
            view.errorToPassword("비밀번호는 8자리 ~ 20자리로 입력해 주세요")
            return
        }

        view.showLoadingDialog()
        val signUpReq = SignInUpReq(
                email = viewModel.email,
                password = viewModel.password,
                nickname = viewModel.nickname,
                signup_from = Contract.auth_fromApp
        )

        ApiModule.api.signUp(signUpReq).on(
                success = { res ->
                    if (!view.isAdded)
                        return@on

                    /* */
                    view.dismissLoadingDialog()

                    /* */
                    val email = viewModel.email
                    val nickname = viewModel.nickname
                    val password = viewModel.password
                    val token = res.token

                    /* */
                    val data = Bundle()
                    try {
                        data.putString(AccountManager.KEY_ACCOUNT_NAME, email)
                        data.putString(AccountManager.KEY_PASSWORD, password)

                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, Contract.ACCOUNT_TYPE)
                        data.putString(AccountManager.KEY_AUTHTOKEN, token)
                    } catch (e: Exception) {
                        data.putString("ERR_MSG", e.message)
                    }

                    val intent = Intent()
                    intent.putExtras(data)

                    /* */
                    finishLogin(intent, nickname)
                },
                fail = { error, throwable ->
                    view.dismissLoadingDialog()

                    if (error?.message == "RequireEmailPassword") {
                        view.toast("이메일과 비밃번호를 입력해주세요")
                        return@on
                    }

                    if (error?.message == "AlreadySignedEmail") {
                        view.errorToEmail("이미 가입된 메일 입니다")
                        return@on
                    }

                    if (throwable is SocketTimeoutException) {
                        view.toast("인터넷 연결을 확인해주세요")
                        return@on
                    }
                }
        )
    }

    /**
     */

    fun reqSignIn() {
        view.showLoadingDialog()
        val signInReq = SignInUpReq(
                email = viewModel.email,
                password = viewModel.password
        )

        ApiModule.api.signIn(signInReq).on(
                success = { res ->
                    if (!view.isAdded)
                        return@on

                    /* */
                    view.dismissLoadingDialog()

                    /* */
                    val email = viewModel.email
                    val password = viewModel.password
                    val nickname = res.user?.nickname ?: ""
                    val profileImage = res.user?.profile_image ?: ""
                    val token = res.token

                    /* */
                    val data = Bundle()
                    try {
                        data.putString(AccountManager.KEY_ACCOUNT_NAME, email)
                        data.putString(AccountManager.KEY_PASSWORD, password)

                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, Contract.ACCOUNT_TYPE)
                        data.putString(AccountManager.KEY_AUTHTOKEN, token)
                    } catch (e: Exception) {
                        data.putString("ERR_MSG", e.message)
                    }

                    val intent = Intent()
                    intent.putExtras(data)

                    /* */
                    finishLogin(intent, nickname, profileImage)
                },
                fail = { error, throwable ->
                    view.dismissLoadingDialog()

                    if (error?.message == "NoUserIsFound") {
                        view.toast("없는 이메일 입니다")
                        return@on
                    }

                    if (error?.message == "WrongPassword") {
                        view.errorToEmail("비밀번호가 틀립니다")
                        return@on
                    }

                    if (throwable is SocketTimeoutException) {
                        view.toast("인터넷 연결을 확인해주세요")
                        return@on
                    }
                }
        )
    }

    private fun finishLogin(intent: Intent, nickname: String, profileImage: String = "") {
        val email = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
        val password = intent.getStringExtra(AccountManager.KEY_PASSWORD)
        val authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN)

        val account = Account(nickname, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE))

        /* */
        MLog.deb("register authToken : ${authToken}")
        view.accountManager.addAccountExplicitly(account, password,
                Bundle().apply {
                    putString("email", email)
                    putString("profile_image", profileImage)
                })
        view.accountManager.setAuthToken(account, "", authToken)

        /* */
        (view.activity as AccountIntroLayout).run {
            intent.putExtra("token", authToken)
            intent.putExtra("email", email)
            intent.putExtra("name", nickname)
            setAccountAuthenticatorResult(intent.extras)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

}
