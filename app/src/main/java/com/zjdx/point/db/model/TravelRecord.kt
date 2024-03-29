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
    val createTime: String,

    @ColumnInfo(name = "travel_type")
    var travelTypes: String = "骑行",

    @ColumnInfo(name = "travel_user")
    var travelUser: String = "Test",

    @ColumnInfo(name = "is_upload")
    var isUpload: Int = 0,

    @ColumnInfo(name = "start_time",)
    var startTime: Long =0,

    @ColumnInfo(name = "end_time")
    var endTime: Long=0


)