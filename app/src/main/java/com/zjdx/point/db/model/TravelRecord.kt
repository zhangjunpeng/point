package com.zjdx.point.db.model

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

@Entity
data class TravelRecord(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "create_time")
    val createTime: Long,

    @ColumnInfo(name = "travel_type")
    val travelTypes:String="骑行",

    @ColumnInfo(name = "travel_user")
    var travelUser:String="Test",

    @ColumnInfo(name = "is_upload")
    var isUpload: Int = 0
)