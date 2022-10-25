package com.zjdx.point.ui.edit

import android.Manifest
import android.os.Bundle
import android.view.View
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.Marker
import com.amap.api.maps2d.model.MarkerOptions
import com.amap.api.maps2d.model.MyLocationStyle
import com.blankj.utilcode.util.ToastUtils
import com.zjdx.point.databinding.ActivityChooseAddressBinding
import com.zjdx.point.event.ChooseEvent
import com.zjdx.point.ui.base.BaseActivity
import org.greenrobot.eventbus.EventBus
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class ChooseAddressActivity : BaseActivity() {


    lateinit var binding: ActivityChooseAddressBinding

    lateinit var map: AMap
    private var marker: Marker? = null
     var isSetAddress:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSetAddress=intent.getBooleanExtra("isSetAddress",false)
        binding.map.onCreate(savedInstanceState)
    }

    override fun initView() {
        binding = ActivityChooseAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.title.leftIvTitleBar.setOnClickListener {
            finish()
        }
        binding.title.middleTvTitleBar.text = if (isSetAddress) {"请在地图上点击您的住址"}else{"请在地图上点击您的工作地址"}
        binding.title.rightIvTitleBar.visibility = View.GONE
        binding.confirm.setOnClickListener {
            if (marker == null) {
                ToastUtils.showShort("请在地图上点击选点")
                return@setOnClickListener
            }
            val event = ChooseEvent(
            )
            event.address =
                marker?.position?.latitude.toString() + "," + marker?.position?.longitude.toString()
            EventBus.getDefault().post(event)
            finish()
        }
        initMapWithPermissionCheck()

        map.setOnMapClickListener {
            if (marker != null) {
                marker?.remove()
            }
            marker = map.addMarker(
                MarkerOptions().position(LatLng(it.latitude, it.longitude)).title("我的住址")
                    .snippet("DefaultMarker")
            )
        }

    }

    @NeedsPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
    )
    fun initMap() {

        map = binding.map.map
        val myLocationStyle: MyLocationStyle = MyLocationStyle()
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(5000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。

        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        map.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW))
        map.setOnMapLoadedListener {
            map.moveCamera(CameraUpdateFactory.zoomTo(18f))
        }

    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onDestroy() {
        binding.map.onDestroy()
        super.onDestroy()
    }
}