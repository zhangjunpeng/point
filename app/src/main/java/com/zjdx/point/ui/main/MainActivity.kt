package com.zjdx.point.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.zjdx.point.NameSpace
import com.zjdx.point.PointApplication
import com.zjdx.point.databinding.ActivityMainBinding
import com.zjdx.point.event.UpdateMsgEvent
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.ui.history.HistoryTravelActivity
import com.zjdx.point.ui.login.LoginActivity
import com.zjdx.point.ui.travel.TravelActivity
import com.zjdx.point.ui.viewmodel.ViewModelFactory
import com.zjdx.point.utils.DateUtil
import com.zjdx.point.utils.DownloadUtils
import com.zjdx.point.utils.SPUtils
import com.zjdx.point.work.PointWorkManager
import com.zjdx.point.work.UploadLocationsWork
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


    val mainViewModel: MainViewModel by viewModels<MainViewModel> {
        ViewModelFactory((application as PointApplication).travelRepository)
    }

    override fun initRootView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        EventBus.getDefault().register(this)
//        initLocationService()
        initLocationServiceWithPermissionCheck()
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
        mainViewModel.findTravelNum()
        addUploadWork()
        mainViewModel.getAppVersion()
    }

    override fun initView() {
        binding.travelMainAc.setOnClickListener {
            startActivity(Intent(this, TravelActivity::class.java))
        }
        binding.historyMainAc.setOnClickListener {
            startActivity(Intent(this, HistoryTravelActivity::class.java))

        }
        binding.leftIvMainAc.setOnClickListener {
            binding.root.openDrawer(Gravity.LEFT)
        }

        binding.logOutMainAc.setOnClickListener {
            SPUtils.getInstance(this).put(NameSpace.UID, "")
            SPUtils.getInstance(this).put(NameSpace.ISLOGIN, false)
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onRestart() {
        super.onRestart()
        mainViewModel.findTravelNum()
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