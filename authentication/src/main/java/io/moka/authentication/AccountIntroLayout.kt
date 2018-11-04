package io.moka.authentication

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.moka.authentication.util.Contract
import io.moka.lib.base.util.*
import io.moka.lib.base.util.log.MLog
import kotlinx.android.synthetic.main.layout_account_intro.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.anko.topPadding


class AccountIntroLayout : AccountAuthenticatorActivity() {

    private val accountManager by lazy { AccountManager.get(this) }
    private val adapter by lazy { AccountAdapter(this) }
    private var noVisible = false

    /**
     * LifeCycle functions
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_account_intro)

        initView()
        bindView()
        initData()
    }

    override fun onBackPressed() {
        (supportFragmentManager.findFragmentByTag(AccountLayout::class.java.simpleName) as? AccountLayout)?.onBackPressed()

        if (noVisible) {
            finish()
        }
        else {
            super.onBackPressed()
        }
    }

    /**
     */

    private fun initView() {
        constraintLayout_container.topPadding = statusBarSize
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        recyclerView.adapter = adapter
    }

    private fun bindView() {
        textView_signIn.onClick { onClickToSignIn() }
        textView_anotherAccount.onClick { onClickAnotherSignIn() }
        textView_signUp.onClick { onClickSignUp() }

        adapter.onSelectedListener = { data -> onSelected(data) }
    }

    private fun initData() {
        val type = intent.getStringExtra(AccountManager.KEY_AUTH_TOKEN_LABEL)
        if (type != Contract.JOIN_DAYDAY) {
            view_container_wall.visible()
            noVisible = true

            onClickAnotherSignIn(true)
            return
        }

        val accounts = accountManager.getAccountsByType(Contract.ACCOUNT_TYPE)

        if (accounts.isEmpty()) {
            view_container_wall.visible()
            noVisible = true

            onClickAnotherSignIn(true)
        }
        else {
            noVisible = false

            accounts.forEach { account ->
                val email = accountManager.getUserData(account, "email")
                val profileImage = accountManager.getUserData(account, "profile_image")
                val nickname = account.name

                val data = AccountAdapter.Data(profileImage, nickname, email)
                adapter.add(data)
            }

            /* */
            adapter.selectedData = adapter.items[0]
            onSelected(adapter.items[0])
            adapter.notifyDataSetChanged()
        }
    }

    private fun onSelected(data: AccountAdapter.Data) {
        textView_signIn.text = spannableText(
                attr("( ", isBold = true, ratio = 0.8f),
                attr("${data.nickname}", isBold = true),
                attr(" )", isBold = true, ratio = 0.8f),
                attr("으로 로그인 하기"))
    }

    private fun getTokensAndFinish(account: Account) = CoroutineScope(Dispatchers.Main).launch {
        val deferred = async(Dispatchers.IO) {
            accountManager.getAuthToken(account, Contract.ACCOUNT_TYPE, null, null, null, null)
        }

        val future = deferred.await()
        async(Dispatchers.IO) {
            val bnd = future.result
            val authToken = bnd.getString(AccountManager.KEY_AUTHTOKEN)
            MLog.deb("token : ${authToken} / name : ${account.name}")

            /* */
            val intent = Intent()
            val data = Bundle()
            data.putString("token", authToken)
            intent.putExtras(data)

            setAccountAuthenticatorResult(data)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }.await()
    }

    private fun onClickToSignIn() {
        val accounts = accountManager.getAccountsByType(Contract.ACCOUNT_TYPE)
        val selectedData = adapter.selectedData

        val account = accounts.filter { accountManager.getUserData(it, "email") == selectedData?.email }[0]
        getTokensAndFinish(account)
    }

    /**
     */

    private fun onClickAnotherSignIn(noSlide: Boolean? = false) {
        supportFragmentManager
                .beginTransaction()
                .apply {
                    if (noSlide == true)
                        setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    else
                        setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right)
                }
                .add(R.id.view_container, AccountLayout().apply { startFrom = AccountLayout.STATE_SIGN_IN }, AccountLayout::class.java.simpleName)
                .addToBackStack(AccountLayout.toString())
                .commitAllowingStateLoss()
    }

    private fun onClickSignUp() {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right)
                .add(R.id.view_container, AccountLayout().apply { startFrom = AccountLayout.STATE_SIGN_UP }, AccountLayout::class.java.simpleName)
                .addToBackStack(AccountLayout.toString())
                .commitAllowingStateLoss()
    }

}
