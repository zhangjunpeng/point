package com.zjdx.point.ui.tagging

import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.zjdx.point.PointApplication
import com.zjdx.point.databinding.ActivityHisDeatailBinding
import com.zjdx.point.event.HisTagEvent
import com.zjdx.point.ui.DBViewModelFactory
import com.zjdx.point.ui.base.BaseActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class HisDeatailActivity : BaseActivity() {

    lateinit var binding: ActivityHisDeatailBinding

    private val hisDetailViewModel: HisDetailViewModel by viewModels<HisDetailViewModel> {
        DBViewModelFactory((application as PointApplication).travelRepository)
    }

    override fun initView() {
        binding = ActivityHisDeatailBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
        EventBus.getDefault().register(this)

        binding.titleBar.leftIvTitleBar.setOnClickListener {
            finish()
        }
        binding.titleBar.middleTvTitleBar.text = "标注详情"
        binding.titleBar.rightIvTitleBar.visibility = View.GONE

    }

    override fun initViewMoedl() {
        hisDetailViewModel.tagRecordLiveData.observe(this) {
            binding.desc.text = it.desc
            binding.dis.text = it.destination
            binding.startTime.text = it.startTime
            binding.endTime.text = it.endTime
            binding.startType.text = it.startType
            binding.endType.text = it.endType
            binding.recyler.layoutManager = object : LinearLayoutManager(this) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            val modList = it.travelmodel!!.split(",")
            binding.recyler.adapter = HisDetailAdapter(this, modList)

        }

    }

    @Subscribe(sticky = true)
    fun onReceiveModel(event: HisTagEvent) {
        hisDetailViewModel.tagRecordLiveData.postValue(event.tagRecord)
        EventBus.getDefault().removeAllStickyEvents()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

}