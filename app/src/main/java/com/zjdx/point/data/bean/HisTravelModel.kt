package com.zjdx.point.data.bean

import com.zjdx.point.db.model.TravelRecord

data class HisTravelModel(
    val msg:String,
    val code:Int,
    val count:Int,
    val list:ArrayList<TravelRecord>
)
