package com.zjdx.point

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.zjdx.point.databinding.ActivityMainBinding
import com.zjdx.point.ui.base.BaseActivity
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : BaseActivity() {

    val TAG="LocationService"
    private val CODE: Int = 3000
    lateinit var binding: ActivityMainBinding

    lateinit var mLocationClient: AMapLocationClient

    override fun initRootView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLocationService()
    }

    override fun initView() {
        binding.startMainAc.setOnClickListener {
            startLoactionService()
        }
    }


    //声明AMapLocationClient类对象

    val mAMapLocationListener = AMapLocationListener { amapLocation ->
        if (amapLocation != null) {
            Log.i(TAG, "errorCode=" + amapLocation.errorCode)
            if (amapLocation.errorCode == 0) {
                //解析定位结果
                Log.i(TAG, "latitude" + amapLocation.latitude.toString())//获取纬度
                Log.i(TAG, "longitude" + amapLocation.longitude.toString())//获取经度
                Log.i(TAG, "locationType" + amapLocation.locationType.toString()) //获取当前定位结果来源，如网络定位结果，详见定位类型表
                Log.i(TAG, "locationDetail" + amapLocation.locationDetail.toString()) //定位信息描述

                Log.i(TAG, "accuracy=" + amapLocation.accuracy.toString())//获取精度信息
                Log.i(TAG, "address=" + amapLocation.address.toString())//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                Log.i(TAG, "country=" + amapLocation.country.toString())//国家信息
                Log.i(TAG, "province=" + amapLocation.province.toString()) //省信息
                Log.i(TAG, "city=" + amapLocation.city.toString())//城市信息

                Log.i(TAG, "district=" + amapLocation.district.toString())//城区信息
                Log.i(TAG, "street=" + amapLocation.street.toString())//街道信息
                Log.i(TAG, "streetNum=" + amapLocation.streetNum.toString())//街道门牌号信息
                Log.i(TAG, "cityCode=" + amapLocation.cityCode.toString())//城市编码


                Log.i(TAG, "altitude=" + amapLocation.altitude.toString())//海拔
                Log.i(TAG, "speed=" + amapLocation.speed.toString())//速度

                Log.i(TAG, "gpsAccuracyStatus=" + amapLocation.gpsAccuracyStatus.toString())//获取GPS的当前状态

                //获取定位时间
                val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val date = Date(amapLocation.time)
                df.format(date)
            }
        } else {
            Log.i("LocationService", "amapLocation null")
        }
    }


    fun initLocationService() {
        //这里以ACCESS_COARSE_LOCATION为例
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
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
                ),
                CODE
            )//自定义的code
        }


        mLocationClient = AMapLocationClient(applicationContext)

        val mLocationOption = AMapLocationClientOption()
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        mLocationOption.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.Transport

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms
        mLocationOption.interval = 10 * 1000

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.isNeedAddress = true


        mLocationClient.setLocationOption(mLocationOption)


        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        mLocationClient.stopLocation()


        mLocationClient.setLocationListener(mAMapLocationListener)


    }

    fun startLoactionService() {

        mLocationClient.startLocation()

//        mLocationClient.stopLocation()

    }


}