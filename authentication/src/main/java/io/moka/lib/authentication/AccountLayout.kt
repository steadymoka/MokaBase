package io.moka.lib.authentication


import android.accounts.AccountManager
import android.app.AlertDialog
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.moka.lib.base.util.*
import io.moka.lib.base.util.log.MLog
import io.moka.lib.base.webview.WebViewActivity
import kotlinx.android.synthetic.main.layout_account.*
import org.jetbrains.anko.support.v4.toast


class AccountLayout : Fragment() {

    companion object Factory {
        const val STATE_SIGN_IN: Int = 1005
        const val STATE_SIGN_UP: Int = 1006
    }

    val viewModel by lazy { ViewModel() }
    private val presenter by lazy { AccountPresenter(this) }
    val accountManager by lazy { AccountManager.get(activity) }

    var startFrom: Int = STATE_SIGN_IN

    /**
     * LifeCycle functions
     */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.layout_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        initView()
        bindView()
    }

    fun onBackPressed() {
        if (!isAdded)
            return

        MLog.deb("AccountLayout onBackPressed() is called")
        editText_email.clearFocus()
        editText_name.clearFocus()
        editText_password.clearFocus()
    }

    /**
     */

    private fun initView() {
        constraintLayout_container.layoutParams.height = deviceHeightPixel - statusBarSize
        viewModel.state = startFrom

        /* */
        val findPassword = SpannableString("비밀번호 찾기")
        findPassword.setSpan(ForegroundColorSpan(color(R.color.red_01)), 0, findPassword.length, Spanned.SPAN_COMPOSING)
        findPassword.setSpan(UnderlineSpan(), 0, findPassword.length, Spanned.SPAN_COMPOSING)

        textView_findPassword.text = SpannableStringBuilder("비밀번호가 기억안나시나요? ").append(findPassword)
    }

    private fun bindView() {
        imageView_backButton.onClick { activity!!.onBackPressed() }
        button_signIn.onClick { onClickSignIn() }
        button_signUp.onClick { onClickSignUp() }
        textView_findPassword.onClick { onClickToFindPassword() }
        textView_signIn.onClick { viewModel.state = STATE_SIGN_IN }
        textView_signUp.onClick { viewModel.state = STATE_SIGN_UP }
        textView_showPrivacy.onClick { WebViewActivity.goWebView(activity!!, "https://haruharu.io/legal/privacy") }
        textView_showTerms.onClick { WebViewActivity.goWebView(activity!!, "https://haruharu.io/legal/terms") }
    }

    /**
     */

    private fun onClickSignIn() {
        if (!checkValidationGeneral())
            return

        presenter.reqSignIn()
    }

    private fun onClickSignUp() {
        if (!checkValidationGeneral())
            return

        if (!checkContract()) {
            toast("약관에 동의해주세요")
            return
        }

        if (editText_name.text.toString().trim().isEmpty()) {
            editText_name.requestFocus()
            editText_name.error = "닉네임을 입력해주세요"
            return
        }

        presenter.reqSignUp()
    }

    private fun onClickToFindPassword() {
        FindPasswordDialogFragment().showDialog(activity!!.supportFragmentManager) {}
    }

    private fun checkValidationGeneral(): Boolean {
        if (!checkEmailValidation())
            return false
        else if (!checkPasswordValidation())
            return false

        return true
    }

    private fun checkContract(): Boolean {
        return checkBox_privacy.isChecked && checkBox_terms.isChecked
    }

    private fun checkEmailValidation(): Boolean {
        val email = editText_email?.text.toString()

        if (TextUtils.isEmpty(email)) {
            editText_email.requestFocus()
            editText_email.error = "이메일은 반드시 입력해야 합니다"
            return false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editText_email.requestFocus()
            editText_email.error = "올바른 형식의 이메일을 입력해 주세요"
            return false
        }

        return true
    }

    private fun checkPasswordValidation(): Boolean {
        val password = editText_password?.text.toString()

        if (TextUtils.isEmpty(password)) {

            editText_password.requestFocus()
            editText_password.error = "비밀번호는 반드시 입력해야 합니다"
            return false
        }
        else if (20 < password.length || 8 > password.length) {

            editText_password.requestFocus()
            editText_password.error = "비밀번호는 8자리 ~ 20자리로 입력해 주세요"
            return false
        }

        return true
    }

    /**
     */

    private val loadingDialog by lazy {
        AlertDialog
                .Builder(this.activity)
                .setView(layoutInflater.inflate(R.layout.dialog_loading, null))
                .setCancelable(false)
                .create()
    }

    fun showLoadingDialog() {
        if (!loadingDialog.isShowing)
            loadingDialog!!.show()
    }

    fun dismissLoadingDialog() {
        if (null != loadingDialog && loadingDialog!!.isShowing)
            loadingDialog!!.dismiss()
    }

    /**
     */

    inner class ViewModel {

        var state: Int = STATE_SIGN_IN
            set(value) {
                field = value

                when (field) {
                    STATE_SIGN_IN -> {
                        editText_name.gone()

                        textView_signIn.alpha = 1f
                        indicator_signIn.visibility = View.VISIBLE

                        textView_signUp.alpha = 0.6f
                        indicator_signUp.visibility = View.INVISIBLE

                        button_signIn.visibility = View.VISIBLE
                        button_signUp.visibility = View.GONE

                        textView_findPassword.visibility = View.VISIBLE
                        checkBox_privacy.visibility = View.GONE
                        textView_showPrivacy.visibility = View.GONE
                        checkBox_terms.visibility = View.GONE
                        textView_showTerms.visibility = View.GONE
                    }
                    STATE_SIGN_UP -> {
                        editText_name.visible()

                        textView_signIn.alpha = 0.6f
                        indicator_signIn.visibility = View.INVISIBLE

                        textView_signUp.alpha = 1f
                        indicator_signUp.visibility = View.VISIBLE

                        button_signIn.visibility = View.GONE
                        button_signUp.visibility = View.VISIBLE

                        textView_findPassword.visibility = View.GONE
                        checkBox_privacy.visibility = View.VISIBLE
                        textView_showPrivacy.visibility = View.VISIBLE
                        checkBox_terms.visibility = View.VISIBLE
                        textView_showTerms.visibility = View.VISIBLE
                    }
                }
            }

        var email: String = ""
            get() = editText_email.text.toString()

        var password: String = ""
            get() = editText_password.text.toString()

        var nickname: String = ""
            get() = editText_name.text.toString()

    }

    fun errorToEmail(errorMessage: String) {
        editText_email.requestFocus()
        editText_email.error = spannableText(attr(errorMessage, 0.8f, R.color.white))
    }

    fun errorToPassword(errorMessage: String) {
        editText_password.requestFocus()
        editText_password.error = spannableText(attr(errorMessage, 0.8f, R.color.white))
    }

}
