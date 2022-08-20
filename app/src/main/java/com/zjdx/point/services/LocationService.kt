package com.zjdx.point.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telephony.CellInfoCdma
import android.telephony.CellInfoGsm
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.zjdx.point.PointApplication
import com.zjdx.point.db.dao.LocationDao
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import com.zjdx.point.event.UpdateMapEvent
import org.greenrobot.eventbus.EventBus
import permissions.dispatcher.RuntimePermissions
import java.text.SimpleDateFormat
import java.util.*


class LocationService : Service() {
    lateinit var mLocationOption: AMapLocationClientOption
    lateinit var mLocationClient: AMapLocationClient


    val travelRecord =
        TravelRecord(createTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date().time))

    lateinit var locationDao:LocationDao

    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationDao= (application as PointApplication).database.locationDao()
        initLocationService()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLoactionService()
        return super.onStartCommand(intent, flags, startId)
    }


    @RequiresApi(Build.VERSION_CODES.R)
    fun initLocationService() {

        mLocationClient = AMapLocationClient(applicationContext)

        mLocationOption = AMapLocationClientOption()
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

    //声明AMapLocationClient类对象

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("MissingPermission")
    val mAMapLocationListener = AMapLocationListener { amapLocation ->
        if (amapLocation != null) {
            Log.i("Tag", "errorCode=" + amapLocation.errorCode)
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

                    val mTelephonyManager =
                        getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    // 返回值MCC + MNC
                    val operator = mTelephonyManager.networkOperator
                    val mcc = Integer.parseInt(operator.substring(0, 3));
                    val mnc = Integer.parseInt(operator.substring(3));
                    val cellList = mTelephonyManager.allCellInfo
                    val cellinfo = cellList.first()
                    var lac = 0
                    var cellId = 0
                    var rssi = 0
                    if (cellinfo is CellInfoGsm) {
                        lac = cellinfo.cellIdentity.lac
                        cellId = cellinfo.cellIdentity.cid
                        rssi = cellinfo.cellSignalStrength.rssi
                    }
                    if (cellinfo is CellInfoCdma) {
                        lac = cellinfo.cellIdentity.networkId
                        cellId = cellinfo.cellIdentity.basestationId
                        rssi = cellinfo.cellSignalStrength.cdmaDbm
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
                        address = amapLocation.address,
                        mcc = mcc,
                        mnc = mnc,
                        lac = lac,
                        cid = cellId,
                        bsss = rssi,
                    )


                    locationDao.insertLocation(loca)
                    val event=UpdateMapEvent()
                    event.tid=travelRecord.id
                    EventBus.getDefault().post(event)

                    Log.i(
                        "TAG",
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

    private fun destroyLocation() {
        mLocationClient.stopLocation()

    }


    override fun onDestroy() {
        destroyLocation()
        super.onDestroy()

    }
}