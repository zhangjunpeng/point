package com.zjdx.point.ui.tagging

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.*
import com.blankj.utilcode.util.ToastUtils
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zjdx.point.PointApplication
import com.zjdx.point.databinding.ActivityTaggingBinding
import com.zjdx.point.databinding.FragmentTagInfoBinding
import com.zjdx.point.ui.DBViewModelFactory
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.utils.DateUtil
import com.zjdx.point.utils.PopWindowUtil

class TaggingActivity : BaseActivity(), OnChartValueSelectedListener {

    lateinit var binding: ActivityTaggingBinding

    var tagInfoBinding: FragmentTagInfoBinding? = null

    lateinit var map: AMap
    private var marker: Marker? = null
    private var polyline: Polyline? = null
    private var options: PolylineOptions? = null
    var startTime: Long = 0
    var endTime: Long = 0

    private val entries = ArrayList<Entry>()


    val fragment = TagInfoFragment.newInstance()


    private val taggingViewModel: TaggingViewModel by viewModels<TaggingViewModel> {
        DBViewModelFactory((application as PointApplication).travelRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTaggingBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.mapviewTaggingAc.onCreate(savedInstanceState)
        initMap()
        onInitView()
        initChart()
        onInitViewModel()
        onInitData()

        //test
//        renderChart()
    }

    private fun onInitData() {

        taggingViewModel.getTagRecordIsNotUpload()
    }

    private fun initChart() {
        binding.chart.setOnChartValueSelectedListener(this)
        binding.chart.setDrawGridBackground(false)

        // no description text
        binding.chart.description.isEnabled = false

        // enable touch gestures
        binding.chart.setTouchEnabled(true)

        // enable scaling and dragging
        binding.chart.isDragEnabled = true
        binding.chart.setScaleEnabled(true)

        // if disabled, scaling can be done on x- and y-axis separately
        binding.chart.setPinchZoom(true)
        binding.chart.setNoDataText("无数据")

        val l: Legend = binding.chart.legend

        // modify the legend ...
        l.form = LegendForm.LINE

        // don't forget to refresh the drawing
        binding.chart.invalidate()
    }

    private fun onInitView() {
        binding.startTime.setOnClickListener {
            PopWindowUtil.instance.showTimePicker(this) { date, view ->
                binding.startTime.text = DateUtil.dateFormat.format(date)
                taggingViewModel.startTime.value=date

            }
        }
        binding.endTime.setOnClickListener {
            PopWindowUtil.instance.showTimePicker(this) { date, view ->
                binding.endTime.text = DateUtil.dateFormat.format(date)
                taggingViewModel.endTime.value=date

            }
        }
        binding.recyler.layoutManager=LinearLayoutManager(this)

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


    private fun onInitViewModel() {
        taggingViewModel.startTime.observe(this) {
            if (taggingViewModel.endTime.value == null) {
                return@observe
            }
            taggingViewModel.getLocationsByTime()
        }
        taggingViewModel.endTime.observe(this) {
            if (taggingViewModel.startTime.value == null) {
                return@observe
            }
            taggingViewModel.getLocationsByTime()
        }
        taggingViewModel.allLication.observe(this) {
            if (taggingViewModel.allLication.value == null || taggingViewModel.allLication.value!!.size == 0) {
                ToastUtils.showLong("该时间段内没有数据，请重新选择时间")
            } else {
                renderLocation()
                renderChart()
            }
        }
        taggingViewModel.selectLoaction.observe(this) {
            if (marker != null) {
                marker?.remove()
            }
            marker = map.addMarker(
                MarkerOptions().position(LatLng(it.lat, it.lng)).title("起点")
                    .snippet("DefaultMarker")
            )
        }

        taggingViewModel.notUpTagRecord.observe(this) {
            binding.recyler.adapter = TagAdapter(this,viewModel = taggingViewModel)
        }
        taggingViewModel.addTag.observe(this) {
            if (it) {
//                val bottom = BottomSheetDialogFragment()
//                tagInfoBinding = FragmentTagInfoBinding.inflate(layoutInflater)
                fragment.show(supportFragmentManager,null)
//                bottom.(tagInfoBinding!!.root)
//                bottom.show()
            } else {
                supportFragmentManager.beginTransaction().hide(fragment)
            }
        }
    }

    private fun renderChart() {

        entries.clear()
        for (i in 0 until taggingViewModel.allLication.value!!.size) {
            //test
            entries.add(Entry(i.toFloat(), (Math.random() * 100).toFloat()))
//            entries.add(Entry(i.toFloat(), taggingViewModel.allLication.value!![i].speed))
        }

//        Collections.sort(entries, EntryXComparator())

        val set1 = LineDataSet(entries, "速度")
        set1.lineWidth = 1.5f
        set1.circleRadius = 4f

        // create a data object with the data sets
        val data = LineData(set1)
        // set data
        binding.chart.data = data
        binding.chart.invalidate()
    }

    private fun renderLocation() {
        val latLngList = ArrayList<LatLng>()

        for (i in 0 until taggingViewModel.allLication.value!!.size) {
            val local = taggingViewModel.allLication.value!![i]
            latLngList.add(LatLng(local.lat, local.lng))
        }

        if (polyline == null) {
            options = PolylineOptions()
            polyline = map.addPolyline(
                options!!.addAll(latLngList).width(10f).color(Color.BLUE)
            )
        } else {
            polyline!!.remove()
            options = PolylineOptions().width(10f).color(Color.BLUE)
            options!!.addAll(latLngList)
            polyline = map.addPolyline(options)
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

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val index = entries.indexOf(e)
        taggingViewModel.selectLoaction.value = taggingViewModel.allLication.value!![index]
    }

    override fun onNothingSelected() {
    }

}