package com.zjdx.point.ui.travel

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.*
import com.zjdx.point.PointApplication
import com.zjdx.point.databinding.ActivityTravelBinding
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.ui.base.BaseActivity
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@RuntimePermissions
class TravelActivity : BaseActivity() {

    lateinit var binding: ActivityTravelBinding
    lateinit var map: AMap
    private var polyline: Polyline? = null
    private lateinit var options: PolylineOptions
//    val locationList = ArrayList<Location>()


    lateinit var mLocationClient: AMapLocationClient

    val TAG = "TravlelActivity"
    val travelRecord =
        TravelRecord(createTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date().time))


    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")


    private val travelViewModel: TravelViewModel by viewModels<TravelViewModel> {
        TravelViewModelFactory((application as PointApplication).travelRepository, travelRecord.id)
    }

    //声明AMapLocationClient类对象

    val mAMapLocationListener = AMapLocationListener { amapLocation ->
        if (amapLocation != null) {
            Log.i(TAG, "errorCode=" + amapLocation.errorCode)
            if (amapLocation.errorCode == 0) {
                try {

                    var source = ""
                    when (amapLocation.locationType) {
                        1 -> {
                            source = "GPS定位"
                        }
                        2 -> {
                            source = "前次定位"
                        }
                        4 -> {
                            source = "缓存定位"
                        }
                        5 -> {
                            source = "Wifi定位"
                        }
                        6 -> {
                            source = "基站定位"
                        }
                        8 -> {
                            source = "离线定位"
                        }
                        9 -> {
                            source = "最后位置"
                        }
                    }
                    val loca = Location(
                        tId = travelRecord.id,
                        lat = amapLocation.latitude,
                        lng = amapLocation.longitude,
                        speed = amapLocation.speed,
                        direction = amapLocation.description,
                        altitude = amapLocation.altitude,
                        accuracy = amapLocation.accuracy,
                        source = source,
                        creatTime = format.format(amapLocation.time),
                        address = amapLocation.address
                    )


                    travelViewModel.repository.insertLocation(loca)
                    travelViewModel.getLocationsById(travelRecord.id)


                    Log.i(
                        TAG,
                        "source==$source"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        } else {
            Log.i("LocationService", "amapLocation null")
        }
    }


    private fun addPointOnMap() {

        val latLngList = ArrayList<LatLng>()
        for (loca in travelViewModel.allLication.value!!) {
            latLngList.add(LatLng(loca.lat, loca.lng))
        }

        if (!this::options.isInitialized) {
            options = PolylineOptions()
            polyline = map.addPolyline(
                options.addAll(latLngList).width(10f).color(Color.BLUE)
            )
        } else {
            polyline!!.remove()
            options.addAll(latLngList)
            polyline = map.addPolyline(options)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.mapviewTarvelAc.onCreate(savedInstanceState)
        initMapWithPermissionCheck()
        initLocationServiceWithPermissionCheck()
        startLoactionService()
    }


    override fun initViewMoedl() {
//        travelViewModel.setQueryId(travelRecord.id)
        travelViewModel.allLication.observe(this, { locations ->
            addPointOnMap()
        })
    }


    @NeedsPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )
    fun initMap() {

        map = binding.mapviewTarvelAc.map

        val myLocationStyle: MyLocationStyle = MyLocationStyle()
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。

        myLocationStyle.interval(2000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。

        map.setMyLocationStyle(myLocationStyle) //设置定位蓝点的Style

        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        map.isMyLocationEnabled = true
        map.setOnMapLoadedListener {
            map.moveCamera(CameraUpdateFactory.zoomTo(18f))
        }

    }

    @NeedsPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )
    fun initLocationService() {

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
        binding = ActivityTravelBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {

        binding.endTravel.setOnClickListener {
//            showProgressDialog()
            travelViewModel.repository.insertTravelRecord(travelRecord)
            finish()


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

    override fun onStop() {
        super.onStop()
        mLocationClient.stopLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        binding.mapviewTarvelAc.onDestroy();
    }

}