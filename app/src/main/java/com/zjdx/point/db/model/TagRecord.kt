package com.zjdx.point.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TagRecord(
    @PrimaryKey(autoGenerate = true) val id: Int,

    @ColumnInfo(name = "destination") val destination: String,

    @ColumnInfo(name = "desc") val desc: String,


    @ColumnInfo(name = "start_time") val startTime: String,

    @ColumnInfo(name = "end_time") val endTime: String,

    @ColumnInfo(name = "start_type") val startType: String,

    @ColumnInfo(name = "end_type") val endType: String,

    @ColumnInfo(name = "travel_model") val travelodel: String,

    @ColumnInfo(name = "isupload", defaultValue = "0") val isupload: Int,


    )
