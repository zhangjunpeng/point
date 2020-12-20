package com.zjdx.point.db.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TravelRecord {
    @PrimaryKey(autoGenerate = true)
    Long id;

    @ColumnInfo(name = "create_time")
    Long createTime;


}