package com.zjdx.point.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.zjdx.point.utils.DownloadUtils
import com.zjdx.point.utils.SPUtils
import com.zjdx.point.work.UploadLocationsWork
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

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
            binding.countnumMainAc.text =
                Html.fromHtml("当前出行数据共 <font color='blue'>${it}</font> 条")
        })
        mainViewModel.travelNotUploadNum.observe(this, {
            binding.countNotUploadNumMainAc.text =
                Html.fromHtml("未上传 <font color='red'>${it}</font> 条")
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
        binding.beginUploadMainAc.setOnClickListener {
            addUploadWork()
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
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadLocationsWork>()
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueue(uploadWorkRequest)

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(uploadWorkRequest.id)
            .observe(this) { workInfo ->
                Log.i("workInfo", workInfo!!.state.toString())
                if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                    mainViewModel.findTravelNum()
                }
            }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getUpdateMsgEvent(event: UpdateMsgEvent) {
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