package io.moka.lib.authentication

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.moka.lib.authentication.util.circle
import io.moka.lib.base.adapter.BaseAdapter
import io.moka.lib.base.adapter.ItemData
import io.moka.lib.base.adapter.RecyclerItemView
import io.moka.lib.base.util.onClick
import io.moka.lib.base.util.visibleOrGone
import kotlinx.android.synthetic.main.view_account_item.view.*

class AccountAdapter constructor(private val context: Context) : BaseAdapter<AccountAdapter.Data, AccountAdapter.ItemView>() {

    var selectedData: Data? = null
    var onSelectedListener: ((Data) -> Unit)? = null

    override fun onCreateContentItemViewHolder(parent: ViewGroup, contentViewType: Int): RecyclerView.ViewHolder {
        return ItemView(context, parent)
    }

    inner class ItemView(context: Context, parent: ViewGroup) :
            RecyclerItemView<Data>(context, LayoutInflater.from(context).inflate(R.layout.view_account_item, parent, false)) {

        init {
            itemView.constraintLayout_container.onClick {
                selectedData = data
                onSelectedListener?.invoke(data)
                notifyDataSetChanged()
            }
        }

        override fun refreshView(data: Data) = with(itemView) {
            imageView_check.visibleOrGone(selectedData == data)
            if (data.profileImage.isNullOrEmpty())
                imageView_profile.circle(context, R.drawable.ig_profile)
            else {
                imageView_profile.circle(context, data.profileImage)
            }

            textView_nickname.text = data.nickname ?: ""
            textView_email.text = data.email ?: ""

            Unit
        }

    }

    data class Data(
            var profileImage: String?,
            var nickname: String?,
            var email: String?

    ) : ItemData

}