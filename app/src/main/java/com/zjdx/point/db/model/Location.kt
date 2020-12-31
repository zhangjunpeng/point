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
    @ColumnInfo(name = "source") val source: String,//来源
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "is_upload", defaultValue = "0") var isUpload: Int = 0,//是否已上传 0未上传，1已上传
    @ColumnInfo(name = "creat_time") val creatTime: String,//采集时间r
//    @ColumnInfo(name = "source_text") val sourceText: String,//来源
) {

}