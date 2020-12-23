package com.zjdx.point.ui.travel

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.CameraPosition
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.MyLocationStyle
import com.zjdx.point.PointApplication
import com.zjdx.point.databinding.ActivityTravlelBinding
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.ui.base.BaseActivity
import java.text.SimpleDateFormat
import java.util.*


class TravlelActivity : BaseActivity() {

    private lateinit var adapter: TravelRecylerAdapter
    lateinit var binding: ActivityTravlelBinding

    lateinit var map: AMap

    lateinit var mLocationClient: AMapLocationClient

    val TAG = "TravlelActivity"
    val travelRecord =
        TravelRecord(createTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date().time))


    private val travelViewModel: TravelViewModel by viewModels<TravelViewModel> {
        TravelViewModelFactory((application as PointApplication).travelRepository, travelRecord.id)
    }

    //声明AMapLocationClient类对象

    val mAMapLocationListener = AMapLocationListener { amapLocation ->
        if (amapLocation != null) {
            Log.i(TAG, "errorCode=" + amapLocation.errorCode)
            if (amapLocation.errorCode == 0) {

                val loca = Location(
                    tId = travelRecord.id,
                    lat = amapLocation.latitude,
                    lng = amapLocation.longitude,
                    speed = amapLocation.speed,
                    direction = amapLocation.description,
                    altitude = amapLocation.altitude,
                    accuracy = amapLocation.accuracy,
                    source = amapLocation.locationType,
                    creatTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(amapLocation.time),
                    address = amapLocation.address
                )
                map.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition(
                            LatLng(
                                amapLocation.latitude,
                                amapLocation.longitude
                            ), 18f, 30f, 0f
                        )
                    )
                )
                try {
                    travelViewModel.repository.insertLocation(loca)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                //解析定位结果
                Log.i(TAG, "latitude" + amapLocation.latitude.toString())//获取纬度
                Log.i(TAG, "longitude" + amapLocation.longitude.toString())//获取经度
                Log.i(
                    TAG,
                    "locationType" + amapLocation.locationType.toString()
                ) //获取当前定位结果来源，如网络定位结果，详见定位类型表
                Log.i(TAG, "locationDetail" + amapLocation.locationDetail.toString()) //定位信息描述

                Log.i(TAG, "accuracy=" + amapLocation.accuracy.toString())//获取精度信息
                Log.i(
                    TAG,
                    "address=" + amapLocation.address.toString()
                )

                Log.i(TAG, "altitude=" + amapLocation.altitude.toString())//海拔
                Log.i(TAG, "speed=" + amapLocation.speed.toString())//速度

                Log.i(
                    TAG,
                    "gpsAccuracyStatus=" + amapLocation.gpsAccuracyStatus.toString()
                )//获取GPS的当前状态


            }
        } else {
            Log.i("LocationService", "amapLocation null")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.mapviewTarvelAc.onCreate(savedInstanceState)
//        initMap()
        initLocationService()
        startLoactionService()

    }

    override fun initViewMoedl() {
//        travelViewModel.setQueryId(travelRecord.id)
        travelViewModel.allLication.observe(this, { locations ->
            locations.let {
                adapter.submitList(it)
            }
        })

        travelViewModel.submitBackBeanLiveData.observe(this, {
            dismissProgressDialog()
            if (it.msg.isNotEmpty()) {
                showAlerDialog(it)
            }
        })
    }



    private fun initLocationService() {

        mLocationClient = AMapLocationClient(applicationContext)

        val mLocationOption = AMapLocationClientOption()
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        mLocationOption.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.Transport

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms
        mLocationOption.interval = 2 * 1000

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.isNeedAddress = true

        mLocationClient.setLocationOption(mLocationOption)

        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        mLocationClient.stopLocation()

        mLocationClient.setLocationListener(mAMapLocationListener)

    }

    fun startLoactionService() {

        mLocationClient.startLocation()


    }

    override fun initRootView() {
        binding = ActivityTravlelBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        binding.recylerTravelAc.layoutManager = LinearLayoutManager(this)
        binding.titleBarTravelAc.rightIvTitleBar.setOnClickListener {
            binding.root.openDrawer(Gravity.RIGHT, true)
        }

        binding.recylerTravelAc.layoutManager = LinearLayoutManager(this)
        adapter = TravelRecylerAdapter(this)

        binding.recylerTravelAc.adapter = adapter


        binding.endTravel.setOnClickListener {
            showProgressDialog()
            travelViewModel.repository.insertTravelRecord(travelRecord)
            travelViewModel.uploadLocation()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.mapviewTarvelAc.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        binding.mapviewTarvelAc.onSaveInstanceState(outState);
    }

    override fun onResume() {
        super.onResume()
        binding.mapviewTarvelAc.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        binding.mapviewTarvelAc.onDestroy();
    }

}