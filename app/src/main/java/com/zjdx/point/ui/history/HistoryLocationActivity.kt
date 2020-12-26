package com.zjdx.point.ui.history

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.CameraPosition
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.Polyline
import com.amap.api.maps2d.model.PolylineOptions
import com.zjdx.point.PointApplication
import com.zjdx.point.databinding.ActivityHistoryLocationBinding
import com.zjdx.point.db.model.Location
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.ui.viewmodel.ViewModelFactory


class HistoryLocationActivity : BaseActivity() {

    lateinit var binding: ActivityHistoryLocationBinding

    val historyLocationViewModel: HistoryLocationViewModel by viewModels<HistoryLocationViewModel> {
        ViewModelFactory((application as PointApplication).travelRepository)
    }

    lateinit var map: AMap
    private var polyline: Polyline? = null
    private lateinit var options: PolylineOptions

    var tid = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.mapHislocaAc.onCreate(savedInstanceState)
    }

    override fun initRootView() {
        binding = ActivityHistoryLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initView() {
        map = binding.mapHislocaAc.map

        map.setOnMapLoadedListener {
            if (tid.isNotEmpty()) {
                historyLocationViewModel.getLocationByTid(tid)
            }
        }

        binding.titleBarHislocaAc.leftIvTitleBar.setOnClickListener { finish() }
        binding.titleBarHislocaAc.middleTvTitleBar.text="历史轨迹查看"
        binding.titleBarHislocaAc.rightIvTitleBar.visibility=View.INVISIBLE

    }

    override fun initViewMoedl() {
        historyLocationViewModel.locationListLiveData.observe(this, {
            addPointsOnMap(it)
        })
    }

    override fun initData() {
        tid = intent.getStringExtra("tid").toString()

    }


    private fun addPointsOnMap(locaList: List<Location>) {
        if (!this::options.isInitialized) {
            options = PolylineOptions().width(10f).color(Color.BLUE)
        } else {
            polyline!!.remove()
        }

        for (item in locaList) {
            options.add(LatLng(item.lat, item.lng))
        }
        polyline = map.addPolyline(options)

        if (locaList.isEmpty()) {
            return
        }
        map.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition(
                    LatLng(locaList.last().lat, locaList.last().lng),
                    18f,
                    30f,
                    0f
                )
            )
        )

    }

    override fun onPause() {
        super.onPause()
        binding.mapHislocaAc.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        binding.mapHislocaAc.onSaveInstanceState(outState);
    }

    override fun onResume() {
        super.onResume()
        binding.mapHislocaAc.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        binding.mapHislocaAc.onDestroy();
    }


}