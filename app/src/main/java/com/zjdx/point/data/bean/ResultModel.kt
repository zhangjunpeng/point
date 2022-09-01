package com.zjdx.point.data.bean

import android.location.Location

data class ResultModel<T>(

    val msg:String,
    val code:Int,
    val count:Int,
    val list:ArrayList<T>
)
