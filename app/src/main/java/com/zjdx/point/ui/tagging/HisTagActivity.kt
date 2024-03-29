package com.zjdx.point.ui.tagging

import android.app.Dialog
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.ComponentDialog
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.zjdx.point.PointApplication
import com.zjdx.point.R
import com.zjdx.point.databinding.ActivityHisTaggingBinding
import com.zjdx.point.databinding.DialogSyncBinding
import com.zjdx.point.event.DeleteEvent
import com.zjdx.point.ui.DBViewModelFactory
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.utils.DateUtil
import com.zjdx.point.utils.PopWindowUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class HisTagActivity : BaseActivity() {

    lateinit var binding: ActivityHisTaggingBinding

    private val hisTagViewModel: HisTagViewModel by viewModels<HisTagViewModel> {
        DBViewModelFactory((application as PointApplication).travelRepository)
    }

    override fun initView() {
        binding = ActivityHisTaggingBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
        EventBus.getDefault().register(this)
        binding.recyler.layoutManager = LinearLayoutManager(this)
        binding.titleBarHistag.leftIvTitleBar.setOnClickListener {
            finish()
        }
        binding.titleBarHistag.middleTvTitleBar.text="历史行程记录"
        binding.titleBarHistag.rightIvTitleBar.setImageResource(R.drawable.shaixuan)
        binding.titleBarHistag.rightIvTitleBar.setOnClickListener {
            initSyncDialog()
        }
        binding.recyler.adapter = HisTagAdapter(this@HisTagActivity, hisTagViewModel)
    }

    override fun initViewMoedl() {
        hisTagViewModel.allTagLiveData.observe(this) {
            syncDialog?.dismiss()
            if (it.isEmpty()) {
                ToastUtils.showShort("无标注数据")
            }
            binding.recyler.adapter!!.notifyDataSetChanged()

        }

        hisTagViewModel.delRest.observe(this) {
            dismissProgressDialog()
            if (it) {
                ToastUtils.showShort("删除成功")
                hisTagViewModel.getTagRecordIsUploadByTime(
                    hisTagViewModel.mStartTime, hisTagViewModel.mEndTime
                )
            }
        }
    }

    override fun initData() {
        hisTagViewModel.getTagRecordIsUpload()
    }


    var syncDialog: Dialog? = null
    var syncDialogBinding: DialogSyncBinding? = null
    private fun initSyncDialog() {
        syncDialog = ComponentDialog(this, android.R.style.Theme_Material_Light_Dialog)
        syncDialogBinding =
            DialogSyncBinding.inflate(LayoutInflater.from(this), binding.root, false)
        syncDialog!!.setContentView(syncDialogBinding!!.root)
        syncDialogBinding!!.confirm.setOnClickListener {
            val startTime = syncDialogBinding!!.startTime.text.toString()
            val endTime = syncDialogBinding!!.endTime.text.toString()
            if (startTime.isEmpty() || endTime.isEmpty()) {
                ToastUtils.showShort("请选择时间")
                return@setOnClickListener
            }
            hisTagViewModel.getTagRecordIsUploadByTime(startTime, endTime)
        }
        syncDialogBinding!!.msg.text = "请选择筛选时间段"
        syncDialogBinding!!.startTime.text = hisTagViewModel.mStartTime
        syncDialogBinding!!.endTime.text = hisTagViewModel.mEndTime
        syncDialogBinding!!.cancel.text = "清除"
        syncDialogBinding!!.cancel.setOnClickListener {
            syncDialogBinding!!.startTime.text = ""
            syncDialogBinding!!.endTime.text = ""
            hisTagViewModel.mStartTime = ""
            hisTagViewModel.mEndTime = ""
            hisTagViewModel.getTagRecordIsUpload()
        }
        syncDialogBinding!!.startTime.setOnClickListener {

            val cel = Calendar.getInstance()
            if (syncDialogBinding!!.startTime.text.isNotEmpty()) {
                cel.time = DateUtil.dateFormat.parse(syncDialogBinding!!.startTime.text.toString())!!
            }
            PopWindowUtil.instance.showTimePicker(
                this,
                selectedDate = cel,
                onTimeSelectListener = { date, view ->
                    (it as TextView).text = DateUtil.dateFormat.format(date)
                },
            )
        }
        syncDialogBinding!!.endTime.setOnClickListener {
            val cel = Calendar.getInstance()
            if (syncDialogBinding!!.endTime.text.isNotEmpty()) {
                cel.time = DateUtil.dateFormat.parse(syncDialogBinding!!.endTime.text.toString())!!
            }
            PopWindowUtil.instance.showTimePicker(
                this,
                selectedDate = cel,
                onTimeSelectListener = { date, view ->
                    (it as TextView).text = DateUtil.dateFormat.format(date)
                },
            )
        }

        syncDialog!!.show()

    }

    @Subscribe
    fun getDeleteEvent(event: DeleteEvent) {
        showProgressDialog()
//        var tempkey=""
//        hisTagViewModel.allTagLiveData.value!!.keys.forEach { key ->
//            if (hisTagViewModel.allTagLiveData.value!![key]!!.size==0){
//                tempkey=key
//            }
//        }
//        if (tempkey!=""){
//            val map= hisTagViewModel.allTagLiveData.value!!.toMutableMap()
//            map.remove(tempkey)
//            hisTagViewModel.allTagLiveData.postValue(map)
//        }


        hisTagViewModel.deleteTag(event.tagid)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}

