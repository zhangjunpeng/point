package com.zjdx.point.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.zjdx.point.PointApplication
import com.zjdx.point.databinding.ActivityMainBinding
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.ui.history.HistoryTravelActivity
import com.zjdx.point.ui.travel.TravelActivity
import com.zjdx.point.ui.viewmodel.ViewModelFactory
import com.zjdx.point.work.UploadLocationsWork
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
        mainViewModel.appVersionModelLiveData.observe(this,{
            showAlerDialog("发现新版本！！")
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


}