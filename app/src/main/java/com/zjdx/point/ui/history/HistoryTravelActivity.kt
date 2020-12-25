package com.zjdx.point.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.zjdx.point.PointApplication
import com.zjdx.point.databinding.ActivityHistoryTravelBinding
import com.zjdx.point.ui.base.BaseActivity

class HistoryTravelActivity : BaseActivity() {

    lateinit var binding: ActivityHistoryTravelBinding

    val historyTravelViewModel: HistoryTravelViewModel by viewModels<HistoryTravelViewModel> {
        HistoryTravelViewModelFactory((application as PointApplication).travelRepository)
    }

    override fun initRootView() {
        binding = ActivityHistoryTravelBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        binding.recyclerHistoryAc.layoutManager=LinearLayoutManager(this)
        binding.recyclerHistoryAc.adapter=
    }




}