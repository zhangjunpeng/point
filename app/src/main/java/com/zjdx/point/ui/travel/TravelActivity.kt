package com.zjdx.point.ui.travel

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.*
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.zjdx.point.NameSpace
import com.zjdx.point.PointApplication
import com.zjdx.point.R
import com.zjdx.point.databinding.ActivityTravelBinding
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.event.TravelEvent
import com.zjdx.point.event.UpdateMapEvent
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.ui.main.MainActivity
import com.zjdx.point.utils.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TravelActivity : BaseActivity() {

    private var marker: Marker? = null
    lateinit var binding: ActivityTravelBinding
    lateinit var map: AMap
    private var polyline: Polyline? = null
    private lateinit var options: PolylineOptions
//    val locationList = ArrayList<Location>()

    var startTime: Long = 0
    var endTime: Long = 0

    companion object {
        var mLocationClient: AMapLocationClient? = null
    }


    val TAG = "TravlelActivity"
    var travelRecord: TravelRecord? = null

    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")


    init {
        cancelListener = View.OnClickListener {
//            val locations=travelViewModel.repository.getLocationListById(travelRecord!!.id)
//            travelViewModel.repository.deteleLocation(locations)
            travelViewModel.deleteTravelRecord(travelRecord!!)
            mLocationClient!!.disableBackgroundLocation(true)
            mLocationClient!!.stopLocation()
            SPUtils.getInstance().put(NameSpace.ISRECORDING, false)
            SPUtils.getInstance().put(NameSpace.RECORDINGID, "")
            startMain(true)
            finish()
        }
        saveListener = View.OnClickListener {

            dismissAbnormalDialog()
            startMain(true)
            finish()
        }
    }

    private fun startMain(upload: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("upload", upload)
        startActivity(intent)
    }

    private fun saveTravelRecord() {
        if (endTime == 0L) {
            endTime = Date().time
        }
        if (endTime - startTime < 60 * 1000) {
            val locations = travelViewModel.repository.getLocationListById(travelRecord!!.id)
            travelViewModel.repository.deteleLocation(locations)
            travelViewModel.repository.deteleTravel(travelRecord!!)
        } else {
            travelRecord!!.endTime = endTime
            travelViewModel.repository.updateTravelRecord(travelRecord!!)
//            val event = UpdateMsgEvent()
//            event.isBeginUpload=true
//            event.msg = "记录完成，开始上传"
//            EventBus.getDefault().post(event)
        }
        mLocationClient!!.disableBackgroundLocation(true)
        mLocationClient!!.stopLocation()

        SPUtils.getInstance().put(NameSpace.ISRECORDING, false)
        SPUtils.getInstance().put(NameSpace.RECORDINGID, "")
    }


    private val travelViewModel: TravelViewModel by viewModels<TravelViewModel> {
        TravelViewModelFactory((application as PointApplication).travelRepository)
    }

    val startListener = View.OnClickListener {
        startTime = Date().time
        travelRecord =
            TravelRecord(
                travelUser = SPUtils.getInstance().getString(NameSpace.UID),
                createTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime),
                startTime = startTime
            )

        travelViewModel.repository.insertTravelRecord(travelRecord!!)
        startLoactionService()
//        startService(Intent(applicationContext,KeepLifeService::class.java))

        binding.endTravel.text = "结束出行"
        binding.endTravel.setOnClickListener(endListener)
        SPUtils.getInstance().put(NameSpace.ISRECORDING, true)
        SPUtils.getInstance().put(NameSpace.RECORDINGID, travelRecord!!.id)

    }
    val endListener = View.OnClickListener {
        endTime = Date().time
        var msg = ""
        if ((endTime - startTime) > 60 * 1000) {
            msg = "是否结束本次出行？"
            showAbnormalDialog(msg, 1)

        } else {
            msg = "是否结束本次出行？出行时间过短，将不保存记录！"
            showAbnormalDialog(msg, 2)

        }
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
                        tId = travelRecord!!.id,
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
                    travelViewModel.getLocationsById(travelRecord!!.id)


//                    val mCameraUpdate = CameraUpdateFactory.newCameraPosition(
//                        CameraPosition.Builder().target(
//                            LatLng(
//                                amapLocation.latitude,
//                                amapLocation.longitude,
//                            )
//                        ).build()
//                    )
//                    map.moveCamera(mCameraUpdate)


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

    private fun addMakerOnMap(loca: LatLng) {

        marker =
            map.addMarker(MarkerOptions().position(loca).title("起点").snippet("DefaultMarker"))

    }


    private fun addPointOnMap() {

        val latLngList = ArrayList<LatLng>()
        for (loca in travelViewModel.allLication.value!!) {
            latLngList.add(LatLng(loca.lat, loca.lng))
        }

        if (marker == null && latLngList.size > 0) {
            addMakerOnMap(latLngList[0])
        }

        if (!this::options.isInitialized) {
            options = PolylineOptions()
            polyline = map.addPolyline(
                options.addAll(latLngList).width(10f).color(Color.BLUE)
            )
        } else {
            polyline!!.remove()
            options = PolylineOptions().width(10f).color(Color.BLUE)
            options.addAll(latLngList)

            polyline = map.addPolyline(options)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        binding.mapviewTarvelAc.onCreate(savedInstanceState)
        initMap()

//        serviceIntent = Intent(this, LocationService::class.java)
//        startService(serviceIntent)
        initLocationService()

        checkIsRecording()
    }

    private fun checkIsRecording() {
        if (SPUtils.getInstance().getBoolean(NameSpace.ISRECORDING, false)) {
            val id = SPUtils.getInstance().getString(NameSpace.RECORDINGID)
            travelRecord = travelViewModel.getTravelRecordById(id)
            startLoactionService()
            binding.endTravel.text = "结束出行"
            binding.endTravel.setOnClickListener(endListener)
        }

    }


    override fun initViewMoedl() {
//        travelViewModel.setQueryId(travelRecord.id)
        travelViewModel.allLication.observe(this, { locations ->
            addPointOnMap()
        })
    }


    fun initMap() {

        map = binding.mapviewTarvelAc.map

        val myLocationStyle: MyLocationStyle = MyLocationStyle()
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。

        myLocationStyle.interval(1000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。


        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        map.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW))
        map.setOnMapLoadedListener {
            map.moveCamera(CameraUpdateFactory.zoomTo(18f))
        }

    }


    fun initLocationService() {

        mLocationClient = AMapLocationClient(this)

        val mLocationOption = AMapLocationClientOption()
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        mLocationOption.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.Transport

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms
        mLocationOption.interval = 1000

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.isNeedAddress = true

//        mLocationOption.isGpsFirst = true

        mLocationClient!!.setLocationOption(mLocationOption)

        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        mLocationClient!!.stopLocation()

        mLocationClient!!.setLocationListener(mAMapLocationListener)

    }


    fun startLoactionService() {

        //启动后台定位，第一个参数为通知栏ID，建议整个APP使用一个
        mLocationClient!!.enableBackgroundLocation(2001, buildNotification())
        mLocationClient!!.startLocation()

    }

    override fun initRootView() {
        binding = ActivityTravelBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        binding.endTravel.text = "开始出行"
        binding.endTravel.setOnClickListener(startListener)
        binding.titleBarTravelAc.leftIvTitleBar.setOnClickListener {
           startMain(false)
        }
        binding.titleBarTravelAc.rightIvTitleBar.visibility = View.INVISIBLE

    }

    override fun onBackPressed() {
        startMain(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdatemapEvent(event: UpdateMapEvent) {
        travelViewModel.getLocationsById(event.tid)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onResumeTravel(travelEvent: TravelEvent) {
        travelRecord = travelEvent.travelRecord
        binding.endTravel.text = "结束出行"
        binding.endTravel.setOnClickListener(endListener)
        EventBus.getDefault().removeStickyEvent(travelEvent)
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
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        binding.mapviewTarvelAc.onDestroy()
        //关闭后台定位，参数为true时会移除通知栏，为false时不会移除通知栏，但是可以手动移除
        saveTravelRecord()
        mLocationClient!!.onDestroy()

        EventBus.getDefault().unregister(this)

        LogUtils.i("onDestroy")

        super.onDestroy()


    }


    private val NOTIFICATION_CHANNEL_NAME = "BackgroundLocation"
    private var notificationManager: NotificationManager? = null
    var isCreateChannel = false

    @SuppressLint("NewApi")
    private fun buildNotification(): Notification? {
        var builder: Notification.Builder? = null
        var notification: Notification? = null
        if (Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            val channelId = packageName
            if (!isCreateChannel) {
                val notificationChannel = NotificationChannel(
                    channelId,
                    NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationChannel.enableLights(true) //是否在桌面icon右上角展示小圆点
                notificationChannel.lightColor = Color.BLUE //小圆点颜色
                notificationChannel.setShowBadge(true) //是否在久按桌面图标时显示此渠道的通知
                notificationManager!!.createNotificationChannel(notificationChannel)
                isCreateChannel = true
            }
            builder = Notification.Builder(applicationContext, channelId)
        } else {
            builder = Notification.Builder(applicationContext)
        }
        builder.setSmallIcon(R.drawable.daohang)
            .setContentTitle(Utils.getAppName(this))
            .setContentText("正在后台运行")
            .setWhen(System.currentTimeMillis())
        notification = if (Build.VERSION.SDK_INT >= 16) {
            builder.build()
        } else {
            return builder.notification
        }
        return notification
    }

}