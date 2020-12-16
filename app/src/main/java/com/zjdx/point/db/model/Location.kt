package com.zjdx.point.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Location(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "t_id") val tId:Int,
        @ColumnInfo(name = "lat") val lat:Double,
        @ColumnInfo(name = "lng") val lng:Double,
        @ColumnInfo(name = "speed") val speed:Double,//速度
        @ColumnInfo(name = "direction") val direction:Double,//方向
        @ColumnInfo(name = "height") val height:Double,//高度
        @ColumnInfo(name = "accuracy" )val accuracy:Double,//精度
        @ColumnInfo(name = "source" )val source:Double,//来源
        @ColumnInfo(name = "creat_time" )val creatTime:Long,//采集时间
        )