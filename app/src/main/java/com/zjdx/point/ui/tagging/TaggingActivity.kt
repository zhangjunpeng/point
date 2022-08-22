package com.zjdx.point.ui.tagging

import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.MyLocationStyle
import com.amap.api.maps2d.model.Polyline
import com.amap.api.maps2d.model.PolylineOptions
import com.zjdx.point.PointApplication
import com.zjdx.point.databinding.ActivityTaggingBinding
import com.zjdx.point.ui.DBViewModelFactory
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.ui.travel.TravelActivity
import com.zjdx.point.ui.travel.TravelViewModel
import com.zjdx.point.utils.DateUtil
import com.zjdx.point.utils.PopWindowUtil
import permissions.dispatcher.RuntimePermissions
import java.text.SimpleDateFormat

class TaggingActivity : BaseActivity() {

    lateinit var binding: ActivityTaggingBinding
    lateinit var map: AMap
    var mLocationClient: AMapLocationClient? = null
    private var polyline: Polyline? = null
    private lateinit var options: PolylineOptions
    var startTime: Long = 0
    var endTime: Long = 0
    val simpleDateFormat=SimpleDateFormat()

    private val taggingViewModel: TaggingViewModel by viewModels<TaggingViewModel> {
        DBViewModelFactory((application as PointApplication).travelRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityTaggingBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.mapviewTaggingAc.onCreate(savedInstanceState)
        initMap()
        onInitView()
    }

    fun onInitView() {
        binding.startTime.setOnClickListener {
            PopWindowUtil.instance.showTimePicker(this){date,view->
                binding.startTime.text= DateUtil.dateFormat.format(date)

            }
        }
        binding.endTime.setOnClickListener {
            PopWindowUtil.instance.showTimePicker(this){date,view->
                binding.endTime.text= DateUtil.dateFormat.format(date)
            }
        }
    }

    private fun initMap() {

        map = binding.mapviewTaggingAc.map
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


    fun onInitViewModel(){
        taggingViewModel.startTime.observe(this){

        }
        taggingViewModel.endTime.observe(this){

        }
    }

    override fun onPause() {
        super.onPause()
        binding.mapviewTaggingAc.onPause()
    }
    override fun onResume() {
        super.onResume()
        binding.mapviewTaggingAc.onResume()
    }
    override fun onDestroy() {
        super.onDestroy()
        binding.mapviewTaggingAc.onDestroy()
    }

}