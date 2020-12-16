package com.zjdx.point.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TravelRecord(
        @PrimaryKey(autoGenerate = true) val id: Long,
        @ColumnInfo(name = "create_time") var createTime:Long
)