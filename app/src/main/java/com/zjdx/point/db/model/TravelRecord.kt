package com.zjdx.point.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TravelRecord(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "uid") val uid: Int,
)