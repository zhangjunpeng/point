package com.zjdx.point.ui.tagging

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.*
import com.blankj.utilcode.util.ToastUtils
import com.github.mikephil.charting.components.IMarker
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.zjdx.point.PointApplication
import com.zjdx.point.R
import com.zjdx.point.databinding.ActivityTaggingBinding
import com.zjdx.point.databinding.FragmentTagInfoBinding
import com.zjdx.point.event.EditTagEvent
import com.zjdx.point.ui.DBViewModelFactory
import com.zjdx.point.ui.base.BaseActivity
import com.zjdx.point.utils.DateUtil
import com.zjdx.point.utils.PopWindowUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.util.*

@RuntimePermissions
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
        EventBus.getDefault().register(this)
        binding.mapviewTaggingAc.onCreate(savedInstanceState)
        initMapWithPermissionCheck()
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

        // if disabled, scaling can be done on x- and y-axis separately
        binding.chart.setPinchZoom(true)
        binding.chart.setNoDataText("无数据")

        val l: Legend = binding.chart.legend

        // modify the legend ...
        l.form = LegendForm.LINE

        binding.chart.axisRight.isEnabled = false
        binding.chart.axisLeft.isEnabled = false
        binding.chart.xAxis.isEnabled = false

        binding.chart.isScaleYEnabled = false

        val marker: IMarker = MyMarkerView(this, R.layout.item_tv)
        binding.chart.marker = marker


        // don't forget to refresh the drawing
        binding.chart.invalidate()
    }

    private fun onInitView() {
        binding.startTime.setOnClickListener {
            val cel = Calendar.getInstance()
            if (taggingViewModel.startTime != null) {
                cel.time = taggingViewModel.startTime!!
            }
            PopWindowUtil.instance.showTimePicker(
                this, type = booleanArrayOf(true, true, true, true, true, false),
                selectedDate = cel,
                onTimeSelectListener = { date, view ->
                    binding.startTime.text = DateUtil.dateFormat.format(date)
                    taggingViewModel.startTime = date

                },
            )
        }
        binding.endTime.setOnClickListener {
            val cel = Calendar.getInstance()
            if (taggingViewModel.endTime != null) {
                cel.time = taggingViewModel.endTime!!
            }
            PopWindowUtil.instance.showTimePicker(
                this, type = booleanArrayOf(true, true, true, true, true, false),
                selectedDate = cel,
                onTimeSelectListener = { date, view ->
                    binding.endTime.text = DateUtil.dateFormat.format(date)
                    taggingViewModel.endTime = date

                },
            )
        }
        binding.recyler.layoutManager = LinearLayoutManager(this)

        binding.cancel.setOnClickListener {
            while (taggingViewModel.notUpTagRecord.value!!.any {
                    it.id == 0
                }) {
                AlertDialog.Builder(this).setMessage("您有记录尚未保存，是否退出？")
                    .setPositiveButton("确定") { dialog, which ->
                        finish()
                    }.setNegativeButton("取消") { dialog, which ->
                        dialog.dismiss()
                    }.create().show()
                return@setOnClickListener
            }
            if (taggingViewModel.hasChange) {
                AlertDialog.Builder(this).setMessage("您有记录尚未保存，是否退出？")
                    .setPositiveButton("确定") { dialog, which ->
                        finish()
                    }.setNegativeButton("取消") { dialog, which ->
                        dialog.dismiss()
                    }.create().show()
                return@setOnClickListener
            }
            finish()

        }
        binding.save.setOnClickListener {
            taggingViewModel.repository.insertTagList(taggingViewModel.notUpTagRecord.value!!)
            taggingViewModel.repository.delDBTag(taggingViewModel.deleList)
            taggingViewModel.hasChange = false
            ToastUtils.showLong("保存成功")
            taggingViewModel.getTagRecordIsNotUpload()
        }
        binding.upload.setOnClickListener {
            if (taggingViewModel.notUpTagRecord.value!!.size == 0) {
                ToastUtils.showLong("请添加标注")
                return@setOnClickListener
            }
            AlertDialog.Builder(this).setMessage("确定上传吗？")
                .setPositiveButton("确定") { dialog, which ->
                    dialog.dismiss()
                    showProgressDialog()
                    taggingViewModel.startUpload()
                }.setNegativeButton("取消") { dialog, which ->
                    dialog.dismiss()
                }.create().show()

        }
        binding.clear.setOnClickListener {
            binding.startTime.text = ""
            binding.endTime.text = ""
            taggingViewModel.endTime = null
            taggingViewModel.startTime = null
            taggingViewModel.allLication.value?.clear()
            renderLocation()
            renderChart()
        }
        binding.query.setOnClickListener {
            if (taggingViewModel.endTime == null) {

                return@setOnClickListener
            }
            if (taggingViewModel.startTime == null) {
                return@setOnClickListener
            }
            taggingViewModel.getLocationsByTime()
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

    @Subscribe
    fun onEditTag(event: EditTagEvent){
        supportFragmentManager.beginTransaction().add(R.id.container_recyler, fragment)
            .commit()
    }


    private fun onInitViewModel() {

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
                taggingViewModel.addingTag=null
                supportFragmentManager.beginTransaction().add(R.id.container_recyler, fragment)
                    .commit()
            } else {
                supportFragmentManager.beginTransaction().hide(fragment)
            }
        }
        taggingViewModel.backResult.observe(this){
            if (it){
                ToastUtils.showShort("上传成功")
            }else{
                ToastUtils.showShort("上传失败")
            }
            dismissProgressDialog()
            taggingViewModel.changeTagRecordIsNotUpload()
        }
    }

    private fun renderChart() {
        if (taggingViewModel.allLication.value == null) {
            return
        }
        entries.clear()
        for (i in 0 until taggingViewModel.allLication.value!!.size) {
            val entry = Entry(i.toFloat(), 0f)
            entry.data = taggingViewModel.allLication.value!![i].creatTime.substring(5)
            entries.add(entry)
        }


        val set1 = LineDataSet(entries, null)
        set1.lineWidth = 2f
        set1.circleRadius = 3f
        set1.setDrawIcons(false)

        set1.setCircleColor(Color.BLUE)

        set1.setValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
            ""
        }

        set1.setDrawVerticalHighlightIndicator(true)
        set1.setDrawHorizontalHighlightIndicator(false)
        set1.highLightColor = Color.RED
        set1.highlightLineWidth = 1f

        // create a data object with the data sets
        val data = LineData(set1)
        // set data
        binding.chart.data = data
        binding.chart.data.isHighlightEnabled = true

        binding.chart.invalidate()
    }

    private fun renderLocation() {
        if (taggingViewModel.allLication.value == null) {
            return
        }
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

    override fun onBackPressed() {
        while (taggingViewModel.notUpTagRecord.value!!.any {
                it.id == 0
            }) {
            AlertDialog.Builder(this).setMessage("您有记录尚未保存，是否退出？")
                .setPositiveButton("确定") { dialog, which ->
                    finish()
                }.setNegativeButton("取消") { dialog, which ->
                    dialog.dismiss()
                }.create().show()
            return
        }
        if (taggingViewModel.hasChange) {
            AlertDialog.Builder(this).setMessage("您有记录尚未保存，是否退出？")
                .setPositiveButton("确定") { dialog, which ->
                    finish()
                }.setNegativeButton("取消") { dialog, which ->
                    dialog.dismiss()
                }.create().show()
            return
        }
        finish()
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
        EventBus.getDefault().unregister(this)
        super.onDestroy()
        binding.mapviewTaggingAc.onDestroy()
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val index = entries.indexOf(e)
        taggingViewModel.selectLoaction.value = taggingViewModel.allLication.value!![index]
        binding.chart.highlightValue(h)
    }


    override fun onNothingSelected() {
        marker?.remove()
    }

}