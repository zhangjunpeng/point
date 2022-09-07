package com.zjdx.point.ui.main

import android.content.Intent
import android.text.Html
import android.view.Gravity
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.PointApplication
import com.zjdx.point.databinding.ActivityMainBinding
import com.zjdx.point.event.UpdateMsgEvent
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.ui.edit.EditUserInfoActivity
import com.zjdx.point.ui.history.HistoryTravelActivity
import com.zjdx.point.ui.setting.SettingActivity
import com.zjdx.point.ui.tagging.HisTagActivity
import com.zjdx.point.ui.tagging.TaggingActivity
import com.zjdx.point.ui.travel.TravelActivity
import com.zjdx.point.ui.viewmodel.ViewModelFactory
import com.zjdx.point.utils.DateUtil
import com.zjdx.point.utils.DownloadUtils
import com.zjdx.point.work.PointWorkManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class MainActivity : BaseActivity() {

    val TAG = "LocationService"
    lateinit var binding: ActivityMainBinding

    var upload = true

    val mainViewModel: MainViewModel by viewModels<MainViewModel> {
        ViewModelFactory((application as PointApplication).travelRepository)
    }

    override fun initRootView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        EventBus.getDefault().register(this)
//        initLocationService()
        upload = intent!!.getBooleanExtra("upload", true)

    }

    override fun initViewMoedl() {
        mainViewModel.travelCountNum.observe(this) {
            updateMainInfo()
        }
        mainViewModel.travelNotUploadNum.observe(this) {
            updateMainInfo()
        }
        mainViewModel.appVersionModelLiveData.observe(this) {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionCode = packageInfo.versionCode
            if (it.list==null||it.list.isEmpty() || versionCode >= it.list[0].version) {
                return@observe
            }
            val alertDialog = AlertDialog.Builder(this).setMessage("发现新版本，请更新！")
                .setPositiveButton("下载") { dialog, which ->
                    dialog.dismiss()
                    DownloadUtils(this, it, "point.apk")
                }.create()
            alertDialog.show()
        }
        binding.recyclerMain.layoutManager = LinearLayoutManager(this)
        binding.recyclerMain.adapter =
            MainRecylerAdapter(this, mainViewModel.uploadMsgLiveData.value!!)
//        mainViewModel.uploadMsgLiveData.observe(this, {
//            binding.recyclerMain.adapter!!.notifyDataSetChanged()
//        })
        mainViewModel.sysUserLiveData.observe(this) {
            if (it.address.isNullOrEmpty() || it.salary.isNullOrEmpty() || it.has_car == null || it.has_bicycle == null || it.has_vehicle == null || it.telphone.isNullOrEmpty()) {
                finish()
                val intent=Intent(this, EditUserInfoActivity::class.java)
                intent.putExtra("isEidit",true)
                startActivity(intent)
            }
        }
    }

    private fun updateMainInfo() {
        val hasUploadNum =
            mainViewModel.travelCountNum.value!! - mainViewModel.travelNotUploadNum.value!!
        binding.countnumMainAc.text =
            Html.fromHtml("已成功上传<font color='blue'>${hasUploadNum}</font>/${mainViewModel.travelCountNum.value!!}条数据")

    }

    override fun initData() {
        mainViewModel.getUserInfo()

        if (!SPUtils.getInstance().getBoolean(NameSpace.ISRECORDING)) {
            mainViewModel.findTravelNum()
            addUploadWork()

        } else {
            if (!upload) {
                Toast.makeText(this, "后台记录数据中。。", Toast.LENGTH_LONG).show()
                return
            }
            sendMsg("检测到未正常退出数据记录，正在处理。")

            val travelRecord = mainViewModel.repository.getTravelRecordById(
                SPUtils.getInstance().getString(NameSpace.RECORDINGID)
            )
            travelRecord?.let {
                val location = mainViewModel.repository.getLastLocationById(it.id)
                if (location==null){
                    mainViewModel.repository.deteleTravel(travelRecord)
                    return@let
                }
                it.endTime = DateUtil.dateFormat.parse(location.creatTime).time
                if (it.endTime - it.startTime > 60 * 1000) {
                    mainViewModel.repository.updateTravelRecord(it)
                } else {
                    sendMsg("记录时间少于60秒，已删除数据")
                    val locas = mainViewModel.repository.getLocationListById(it.id)
//                    mainViewModel.repository.deteleLocation(locas)
                    mainViewModel.repository.deteleTravel(it)
                }
            }
            SPUtils.getInstance().put(NameSpace.ISRECORDING, false)
            SPUtils.getInstance().put(NameSpace.RECORDINGID, "")
            addUploadWork()

        }

        mainViewModel.getAppVersion()
    }


    private fun sendMsg(msg: String) {
        val event = UpdateMsgEvent()
        event.msg = msg
        EventBus.getDefault().post(event)
    }

    override fun initView() {
        binding.ziliaoMainAc.setOnClickListener {
            startActivity(Intent(this, EditUserInfoActivity::class.java))
        }
        binding.settingMainAc.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        binding.travelMainAc.setOnClickListener {
            startActivity(Intent(this, TravelActivity::class.java))
            finish()
        }

        binding.taggingMainAc.setOnClickListener {
            startActivity(Intent(this, TaggingActivity::class.java))
        }
        binding.historyMainAc.setOnClickListener {
            startActivity(Intent(this, HistoryTravelActivity::class.java))
        }
        binding.historyTgMainAc.setOnClickListener {
            startActivity(Intent(this, HisTagActivity::class.java))
        }
        binding.leftIvMainAc.setOnClickListener {
            binding.root.openDrawer(Gravity.LEFT)
        }


    }

    override fun onRestart() {
        super.onRestart()
    }

    fun addUploadWork() {
        val request = PointWorkManager.instance.addUploadWork(this)
//        WorkManager.getInstance(this).getWorkInfoByIdLiveData(request!!.id)
//            .observe(this) { workInfo ->
//                Log.i("workInfo", workInfo!!.state.toString())
//                if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
//                    mainViewModel.findTravelNum()
//                }
//            }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getUpdateMsgEvent(event: UpdateMsgEvent) {
        mainViewModel.uploadMsgLiveData.value!!.add(DateUtil.dateFormat.format(Date().time) + ":" + event.msg)
        binding.recyclerMain.adapter!!.notifyDataSetChanged()
        mainViewModel.findTravelNum()
    }



    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)

    }


}