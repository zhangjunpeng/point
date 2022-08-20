package com.zjdx.point.ui.tagging

import android.view.LayoutInflater
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.model.Polyline
import com.amap.api.maps2d.model.PolylineOptions
import com.zjdx.point.databinding.ActivityTaggingBinding
import com.zjdx.point.ui.base.BaseActivity

class TaggingActivity : BaseActivity() {

    lateinit var binding: ActivityTaggingBinding
    lateinit var map: AMap
    private var polyline: Polyline? = null
    private lateinit var options: PolylineOptions
    var startTime: Long = 0
    var endTime: Long = 0

    override fun initRootView() {
        binding=ActivityTaggingBinding.inflate(LayoutInflater.from(this))
    }


}