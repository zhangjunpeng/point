package com.zjdx.point

import android.app.Application
import androidx.room.Room
import com.zjdx.point.db.MyDataBese

class PointApplication : Application() {


    lateinit var db: MyDataBese
    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            MyDataBese::class.java, "point"
        ).build()
    }
}