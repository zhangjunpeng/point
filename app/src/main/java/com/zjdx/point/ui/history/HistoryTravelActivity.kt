package com.zjdx.point.ui.history

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentDialog
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.PointApplication
import com.zjdx.point.R
import com.zjdx.point.databinding.ActivityHistoryTravelBinding
import com.zjdx.point.databinding.DialogSyncBinding
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.ui.base.BaseListViewModel
import com.zjdx.point.ui.viewmodel.ViewModelFactory
import com.zjdx.point.utils.DateUtil
import com.zjdx.point.utils.PopWindowUtil

class HistoryTravelActivity : BaseActivity() {

    lateinit var binding: ActivityHistoryTravelBinding

    private val historyTravelViewModel: HistoryTravelViewModel by viewModels<HistoryTravelViewModel> {
        ViewModelFactory((application as PointApplication).travelRepository)
    }

    private val baseListViewModel: BaseListViewModel by viewModels<BaseListViewModel> {
        ViewModelFactory((application as PointApplication).travelRepository)
    }

    override fun initRootView() {
        binding = ActivityHistoryTravelBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initViewMoedl() {
        historyTravelViewModel.allRecordLiveData.observe(this) {
            dismissProgressDialog()
            binding.recyclerHistoryAc.adapter =
                HistoryRecylerAdapter(this, historyTravelViewModel.allRecordLiveData.value!!)
            binding.swipeHistoryAc.isRefreshing = false
        }
        baseListViewModel.qualityListSreenLiveData.observe(this) {
            refeashData()
        }

        historyTravelViewModel.hisRecordLiveData.observe(this){
            dismissProgressDialog()
            if (it.isEmpty()) {
                syncDialogBinding?.info?.text = "选择的时间段内没有出行记录"
                syncDialogBinding?.syncInfo?.visibility = View.GONE

            } else {
                syncDialogBinding?.info?.text = "查询到${it.size}条数据，开始同步："
                syncDialogBinding?.syncInfo?.visibility = View.VISIBLE
                historyTravelViewModel.startSync()
            }
        }
        historyTravelViewModel.syncIndex.observe(this) {
            if (it == -1) {
                syncDialogBinding?.syncInfo?.visibility = View.GONE
                return@observe
            }
            syncDialogBinding?.syncInfo?.visibility = View.VISIBLE
            syncDialogBinding?.syncInfo?.text = "正在同步第${it + 1}条记录："
        }
        historyTravelViewModel.syncLocationCount.observe(this) {
            if (it > 0) {
                syncDialogBinding?.syncLocationInfo?.visibility = View.VISIBLE
                syncDialogBinding?.syncLocationInfo?.text = "已同步${it}条点位记录"
            }
        }
        historyTravelViewModel.syncComplete.observe(this) {
            if (it) {
                syncDialog?.dismiss()
                ToastUtils.showLong("同步完成")
                refeashData()
            }
        }
    }

    override fun initView() {
        binding.titleBarHistory.middleTvTitleBar.text = "历史出行"
        binding.titleBarHistory.leftIvTitleBar.setOnClickListener { finish() }
        binding.titleBarHistory.rightIvTitleBar.visibility = View.VISIBLE
        binding.titleBarHistory.rightIvTitleBar.setImageResource(R.drawable.shaixuan)
        binding.titleBarHistory.rightIvTitleBar.setPadding(0, 10, 0, 10)
        binding.recyclerHistoryAc.layoutManager = LinearLayoutManager(this)

        binding.titleBarHistory.rightIvTitleBar.setOnClickListener {
            showSreenPopWindow(binding.root)
        }
        binding.swipeHistoryAc.setOnRefreshListener {
            refeashData()
        }
        binding.titleBarHistory.sync.setOnClickListener {
            initSyncDialog()
        }
    }


    var syncDialog: Dialog? = null
    var syncDialogBinding:DialogSyncBinding?=null
    private fun initSyncDialog() {
        syncDialog = ComponentDialog(this, android.R.style.Theme_Material_Light_Dialog)
        syncDialogBinding =
            DialogSyncBinding.inflate(LayoutInflater.from(this), binding.root, false)
        syncDialog!!.setContentView(syncDialogBinding!!.root)
        syncDialogBinding!!.confirm.setOnClickListener {
            val startTime=syncDialogBinding!!.startTime.text.toString()
            val endTime=syncDialogBinding!!.endTime.text.toString()
            if (startTime.isEmpty()||endTime.isEmpty()){
                ToastUtils.showShort("请选择时间")
                return@setOnClickListener
            }
            showProgressDialog()
            historyTravelViewModel.getTravelList(startTime,endTime)

        }
        syncDialogBinding!!.cancel.setOnClickListener {
            syncDialog!!.dismiss()
        }
        syncDialogBinding!!.startTime.setOnClickListener {
            PopWindowUtil.instance.showTimePicker(
                this,
                onTimeSelectListener = { date, view ->
                    ( it as TextView).text=DateUtil.dateFormat.format(date)
                },
            )
        }
        syncDialogBinding!!.endTime.setOnClickListener {
            PopWindowUtil.instance.showTimePicker(
                this,
                onTimeSelectListener = { date, view ->
                    ( it as TextView).text=DateUtil.dateFormat.format(date)
                },
            )
        }


        syncDialog!!.show()

    }

    override fun initPopWindow() {
        sreenPopWindow = PopWindowUtil.instance.createMergePopWindow(
            this, baseListViewModel
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