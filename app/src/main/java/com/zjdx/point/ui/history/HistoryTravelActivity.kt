package com.zjdx.point.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.PointApplication
import com.zjdx.point.R
import com.zjdx.point.databinding.ActivityHistoryTravelBinding
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.ui.base.BaseListViewModel
import com.zjdx.point.ui.viewmodel.ViewModelFactory
import com.zjdx.point.utils.PopWindowUtil
import kotlinx.coroutines.launch

class HistoryTravelActivity : BaseActivity() {

    lateinit var binding: ActivityHistoryTravelBinding

    val historyTravelViewModel: HistoryTravelViewModel by viewModels<HistoryTravelViewModel> {
        ViewModelFactory((application as PointApplication).travelRepository)
    }

    val baseListViewModel: BaseListViewModel by viewModels<BaseListViewModel> {
        ViewModelFactory((application as PointApplication).travelRepository)
    }

    override fun initRootView() {
        binding = ActivityHistoryTravelBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initViewMoedl() {
        historyTravelViewModel.allRecordLiveData.observe(this, {
            dismissProgressDialog()
            binding.recyclerHistoryAc.adapter =
                HistoryRecylerAdapter(this, historyTravelViewModel.allRecordLiveData.value!!)
            binding.swipeHistoryAc.isRefreshing = false
        })
        baseListViewModel.qualityListSreenLiveData.observe(this, {
            refeashData()
        })
    }

    override fun initView() {
        binding.titleBarHistory.middleTvTitleBar.text = "历史出行"
        binding.titleBarHistory.leftIvTitleBar.setOnClickListener { finish() }
        binding.titleBarHistory.rightIvTitleBar.visibility = View.VISIBLE
        binding.titleBarHistory.rightIvTitleBar.setImageResource(R.drawable.shaixuan)
        binding.titleBarHistory.rightIvTitleBar.setPadding(0,10,0,10)
        binding.recyclerHistoryAc.layoutManager =
            LinearLayoutManager(this)

        binding.titleBarHistory.rightIvTitleBar.setOnClickListener {
            showSreenPopWindow(binding.root)
        }
        binding.swipeHistoryAc.setOnRefreshListener {
            refeashData()
        }
    }

    override fun initPopWindow() {
        sreenPopWindow =
            PopWindowUtil.instance.createMergePopWindow(
                this,
                baseListViewModel
            )
    }

    fun refeashData() {
        showProgressDialog()
        val baseListSreen = baseListViewModel.qualityListSreenLiveData.value
        historyTravelViewModel.getAllTravelRecord(
            SPUtils.getInstance().getString(NameSpace.UID),
            baseListSreen!!.start_time,
            baseListSreen!!.end_time
        )
    }


}