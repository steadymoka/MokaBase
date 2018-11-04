package io.moka.lib.base.base


import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import io.moka.lib.base.util.log.MLog


abstract class BaseWideDialogFragment : AppCompatDialogFragment() {

    var onDismiss: (() -> Unit)? = null

    /**
     * Dialog LifeCycle
     * onCreate -> onCreateDialog -> onCreateView -> onViewCreated -> onResume
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(DialogFragment.STYLE_NO_TITLE, 0)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MLog.a("Dialog", "onCreateView called #####")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MLog.a("Dialog", "onViewCreated called #####")
    }

    override fun onResume() {
        super.onResume()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun show(manager: FragmentManager, tag: String) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commit()
        } catch (e: IllegalStateException) {
            MLog.deb("Ignore Can not perform this action after onSaveInstanceStateAsk Question")
        }

    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        onDismiss?.invoke()
    }

}
