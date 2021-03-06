package com.zjdx.point.ui.main

import android.Manifest
import android.content.Intent
import android.text.Html
import android.view.Gravity
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.blankj.utilcode.util.SPUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.PointApplication
import com.zjdx.point.databinding.ActivityMainBinding
import com.zjdx.point.event.UpdateMsgEvent
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.ui.edit.EditUserInfoActivity
import com.zjdx.point.ui.history.HistoryTravelActivity
import com.zjdx.point.ui.login.LoginActivity
import com.zjdx.point.ui.setting.SettingActivity
import com.zjdx.point.ui.travel.TravelActivity
import com.zjdx.point.ui.viewmodel.ViewModelFactory
import com.zjdx.point.utils.DateUtil
import com.zjdx.point.utils.DownloadUtils
import com.zjdx.point.work.PointWorkManager
import kotlinx.coroutines.launch
import okhttp3.internal.format
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.util.*


@RuntimePermissions
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
        initLocationServiceWithPermissionCheck()
        upload = intent!!.getBooleanExtra("upload", true)

    }

    override fun initViewMoedl() {
        mainViewModel.travelCountNum.observe(this, {
            updateMainInfo()
        })
        mainViewModel.travelNotUploadNum.observe(this, {
            updateMainInfo()
        })
        mainViewModel.appVersionModelLiveData.observe(this, {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionCode = packageInfo.versionCode
            if (it.list.isEmpty() || versionCode >= it.list[0].version) {
                return@observe
            }
            val alertDialog = AlertDialog.Builder(this)
                .setMessage("发现新版本，请更新！")
                .setPositiveButton("下载") { dialog, which ->
                    dialog.dismiss()
                    DownloadUtils(this, it, "point.apk")
                }
                .create()
            alertDialog.show()
        })
        binding.recyclerMain.layoutManager = LinearLayoutManager(this)
        binding.recyclerMain.adapter =
            MainRecylerAdapter(this, mainViewModel.uploadMsgLiveData.value!!)
//        mainViewModel.uploadMsgLiveData.observe(this, {
//            binding.recyclerMain.adapter!!.notifyDataSetChanged()
//        })
    }

    private fun updateMainInfo() {
        val hasUploadNum =
            mainViewModel.travelCountNum.value!! - mainViewModel.travelNotUploadNum.value!!
        binding.countnumMainAc.text =
            Html.fromHtml("已成功上传<font color='blue'>${hasUploadNum}</font>/${mainViewModel.travelCountNum.value!!}条数据")

    }

    override fun initData() {

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
        binding.historyMainAc.setOnClickListener {
            startActivity(Intent(this, HistoryTravelActivity::class.java))
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


    @NeedsPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )
    fun initLocationService() {
        //这里以ACCESS_COARSE_LOCATION为例
        Toast.makeText(this, "成功申请", Toast.LENGTH_LONG)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)

    }


}