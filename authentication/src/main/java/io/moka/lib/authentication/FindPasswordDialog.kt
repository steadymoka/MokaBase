package io.moka.lib.authentication


import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import io.moka.lib.authentication.server.api.ApiModule
import io.moka.lib.authentication.server.req.FindPasswordReq
import io.moka.lib.authentication.util.on
import io.moka.lib.base.base.BaseWideDialogFragment
import io.moka.lib.base.util.gone
import io.moka.lib.base.util.onClick
import io.moka.lib.base.util.visible
import kotlinx.android.synthetic.main.dialog_find_password.*
import org.jetbrains.anko.support.v4.toast


internal class FindPasswordDialog : BaseWideDialogFragment() {

    private var callback: (() -> Unit)? = null

    /**
     * LifeCycle function
     */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        this.isCancelable = false
        return inflater.inflate(R.layout.dialog_find_password, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    /**
     */

    private fun initView() {
        textView_find.onClick { onClickFindPassword() }
        textView_ok.onClick { onClickOk() }
        textView_result.onClick {
            textView_title.text = "가입하신 이메일을 입력해주세요"

            editText_email.visible()
            textView_find.visible()
            textView_kakaotalk.visible()
            textView_result.gone()
        }
    }

    /**
     * Click Listener
     */

    private fun onClickFindPassword() {
        val email = editText_email.text.toString()

        if (email.isEmpty()) {
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("이메일 형식을 맞춰주세요")
            return
        }

        /* */

        editText_email.gone()
        textView_find.gone()
        progressBar_loading.visible()

        val onComplete = {
            textView_title.text = "비밀번호 찾기"

            progressBar_loading.gone()
            textView_kakaotalk.gone()
        }

        ApiModule.api.findPassword(findPasswordReq = FindPasswordReq(email)).on(
            success = {
                onComplete()

                textView_result.visible()
                textView_result.text = "해당 이메일로 변경된 비밀번호를 발송하였습니다\n확인후 비밀번호를 변경해주세요 !"
            },
            fail = { error, throwable ->
                onComplete()

                textView_result.visible()
                textView_result.text = "무언가 잘못 되었습니다 😂"
                textView_title.text = ""

                if (error?.message == "NoUserIsFound") {
                    textView_result.text = "해당 이메일이 없습니다 😂  다시 찾아보기!"
                }
            },
            filter = { isAdded }
        )
    }

    private fun onClickOk() {
        callback?.invoke()
        dismiss()
    }

    /**
     */

    fun showDialog(fragmentManager: FragmentManager, callback: () -> Unit) {
        this.callback = callback
        show(fragmentManager, "FindPasswordDialog")
    }

}
