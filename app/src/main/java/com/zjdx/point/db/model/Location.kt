package com.zjdx.point.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity
data class Location(
    @PrimaryKey
    val lid: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "t_id") var tId: String,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lng") val lng: Double,
    @ColumnInfo(name = "speed") val speed: Float,//速度
    @ColumnInfo(name = "direction") val direction: String,//方向
    @ColumnInfo(name = "altitude") val altitude: Double,//高度
    @ColumnInfo(name = "accuracy") val accuracy: Float,//精度
    @ColumnInfo(name = "source") val source: Int=0,//来源
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "creat_time") val creatTime: String,//采集时间
    @ColumnInfo(name = "source_text") val sourceText: String,//来源
) {

}