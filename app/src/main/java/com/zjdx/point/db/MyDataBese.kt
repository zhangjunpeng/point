package com.zjdx.point.db

import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord

@Database(entities = arrayOf(Location::class,TravelRecord::class), version = 1)
abstract class MyDataBese : RoomDatabase() {
}