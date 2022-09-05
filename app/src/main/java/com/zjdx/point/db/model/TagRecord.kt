package com.zjdx.point.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TagRecord(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,

    @ColumnInfo(name = "destination") var destination: String = "",

    @ColumnInfo(name = "desc") var desc: String = "",


    @ColumnInfo(name = "start_time") var startTime: String = "",

    @ColumnInfo(name = "end_time") var endTime: String = "",

    @ColumnInfo(name = "start_type") var startType: String = "",

    @ColumnInfo(name = "end_type") var endType: String = "",

    @ColumnInfo(name = "travel_model") var travelmodel: String = "",

    @ColumnInfo(name = "isupload", defaultValue = "0") var isupload: Int = 0,


    )
