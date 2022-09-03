package com.zjdx.point.data.bean

import com.zjdx.point.db.model.Location


data class HisLocationModel(
    val msg:String,
    val code:Int,
    val count:Int,
    val list:ArrayList<Location>
)
