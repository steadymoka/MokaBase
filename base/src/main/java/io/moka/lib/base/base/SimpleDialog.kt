package io.moka.lib.base.base


import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import io.moka.lib.base.R
import io.moka.lib.base.util.color
import io.moka.lib.base.util.gone
import io.moka.lib.base.util.onClick
import io.moka.lib.base.util.visible
import kotlinx.android.synthetic.main.dialog_simple.*


class SimpleDialog : DialogFragment() {

    var callback: ((isOk: Boolean) -> Unit)? = null
    var onDismissCallback: (() -> Unit)? = null
    var leftClickCallback: (() -> Unit)? = null

    var iconResId = 0
    var message: CharSequence = ""
    var messageAlign: Int = Gravity.CENTER

    var okText = ""
    var cancelText = ""
    var leftText = ""

    var cancelTextColorResId: Int = 0
    var okTextColorResId: Int = 0

    var dissmissIsCancel: Boolean = false
    var isCancelVisible: Boolean = true

    var canCancelable: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_simple, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        button_cancel.onClick { onClickCancel() }
        button_ok.onClick { onClickOk() }
        button_left.onClick { onClickLeftButton() }
    }

    /**
     */

    private fun initView() {
        if (iconResId == 0)
            imageView_icon.visibility = View.GONE
        else {
            imageView_icon.setImageResource(iconResId)
        }

        if (!message.toString().isEmpty())
            textView_message.text = message

        textView_message.gravity = messageAlign

        if (!okText.isEmpty())
            button_ok.text = okText

        if (!cancelText.isEmpty())
            button_cancel.text = cancelText

        if (!isCancelVisible)
            button_cancel.visibility = View.GONE
        else
            button_cancel.visibility = View.VISIBLE

        if (!leftText.isEmpty()) {
            button_left.visible()
            button_left.text = leftText
        }
        else
            button_left.gone()

        if (cancelTextColorResId != 0)
            button_cancel.setTextColor(color(cancelTextColorResId))

        if (okTextColorResId != 0)
            button_ok.setTextColor(color(okTextColorResId))

        if (!canCancelable) {
            isCancelable = false
            dialog.setCanceledOnTouchOutside(false)
        }
    }

    /**
     */

    private fun onClickCancel() {
        if (!isAdded)
            return
        callback?.invoke(false)
        dismiss()
    }

    private fun onClickOk() {
        if (!isAdded)
            return
        callback?.invoke(true)
        dismiss()
    }

    private fun onClickLeftButton() {
        if (!isAdded)
            return
        leftClickCallback?.invoke()
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        onDismissCallback?.invoke()

        if (!isAdded)
            return

        if (dissmissIsCancel)
            onClickCancel()
    }

    /**
     */

    fun changeMessage(message: CharSequence) {
        if (isAdded)
            textView_message.text = message
    }

    /**
     */

    fun showDialog(fragmentManager: FragmentManager, callback: (Boolean) -> Unit) {
        this.callback = callback
        show(fragmentManager, "SimpleDialog")
    }

    fun addOnDismiss(onDismissCallback: () -> Unit): SimpleDialog {
        this.onDismissCallback = onDismissCallback
        return this
    }

    fun setIconResId(iconResId: Int): SimpleDialog {
        this.iconResId = iconResId
        return this
    }

    fun setMessage(message: CharSequence? = ""): SimpleDialog {
        this.message = message!!
        return this
    }

    fun setOkText(okText: String? = ""): SimpleDialog {
        this.okText = okText!!
        return this
    }

    fun setDissMissIsCancel(): SimpleDialog {
        this.dissmissIsCancel = true
        return this
    }

    fun setMessageAlign(messageAlign: Int = Gravity.CENTER): SimpleDialog {
        this.messageAlign = messageAlign
        return this
    }

    fun setIsCancelVisible(isCancelVisible: Boolean): SimpleDialog {
        this.isCancelVisible = isCancelVisible
        return this
    }

    fun setCancelText(cancelText: String? = ""): SimpleDialog {
        this.cancelText = cancelText!!
        return this
    }

}
