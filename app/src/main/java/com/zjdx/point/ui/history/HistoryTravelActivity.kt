package com.zjdx.point.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.zjdx.point.PointApplication
import com.zjdx.point.databinding.ActivityHistoryTravelBinding
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.ui.viewmodel.ViewModelFactory

class HistoryTravelActivity : BaseActivity() {

    lateinit var binding: ActivityHistoryTravelBinding

    val historyTravelViewModel: HistoryTravelViewModel by viewModels<HistoryTravelViewModel> {
        ViewModelFactory((application as PointApplication).travelRepository)
    }

    override fun initRootView() {
        binding = ActivityHistoryTravelBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initViewMoedl() {
        historyTravelViewModel.allRecordLiveData.observe(this, {
            binding.recyclerHistoryAc.adapter =
                HistoryRecylerAdapter(this, historyTravelViewModel.allRecordLiveData.value!!)
            binding.swipeHistoryAc.isRefreshing = false
        })
    }

    override fun initView() {
        binding.titleBarHistory.middleTvTitleBar.text = "历史出行"
        binding.titleBarHistory.leftIvTitleBar.setOnClickListener { finish() }
        binding.titleBarHistory.rightIvTitleBar.visibility = View.INVISIBLE

        binding.recyclerHistoryAc.layoutManager =
            LinearLayoutManager(this)

        binding.swipeHistoryAc.setOnRefreshListener {
            initData()
        }
    }

    override fun initData() {
        historyTravelViewModel.allRecordLiveData.value = historyTravelViewModel.getAllTravelRecord()
    }


}