package io.moka.mokabase

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import kotlinx.android.synthetic.main.activity_recycler_view.*

class RecyclerAdapterActivity : AppCompatActivity() {

    private val adapter by lazy { TestAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
        recyclerView.adapter = adapter

        val datas = arrayListOf<TestAdapter.Data>()
        datas.add(TestAdapter.Data(TestAdapter.Type.ITEM_VIEW, "11111"))
        datas.add(TestAdapter.Data(TestAdapter.Type.ITEM_VIEW, "2222"))
        datas.add(TestAdapter.Data(TestAdapter.Type.ITEM_VIEW, "3333"))
        datas.add(TestAdapter.Data(TestAdapter.Type.ITEM_VIEW, "4444"))
        datas.add(TestAdapter.Data(TestAdapter.Type.ITEM_VIEW, "555"))
        datas.add(TestAdapter.Data(TestAdapter.Type.ITEM_VIEW, "6666"))
        adapter.items = datas

        Handler().postDelayed(1000) {
            val datas = arrayListOf<TestAdapter.Data>()
            datas.add(TestAdapter.Data(TestAdapter.Type.ITEM_VIEW, "11111"))
            datas.add(TestAdapter.Data(TestAdapter.Type.ITEM_VIEW, "2222"))
            datas.add(TestAdapter.Data(TestAdapter.Type.ITEM_VIEW, "3333"))
            datas.add(TestAdapter.Data(TestAdapter.Type.ITEM_VIEW, "4444"))
            datas.add(TestAdapter.Data(TestAdapter.Type.ITEM_VIEW, "555"))
            datas.add(TestAdapter.Data(TestAdapter.Type.ITEM_VIEW, "6666"))
            adapter.items = datas
        }

        refresh.setOnRefreshListener {
            Handler().postDelayed(1000) {
                refresh.setRefreshing(false)
            }
        }
    }

}
