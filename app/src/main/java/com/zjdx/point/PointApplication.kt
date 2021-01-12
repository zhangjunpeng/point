package com.zjdx.point

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.blankj.utilcode.util.Utils
import com.zjdx.point.data.repository.LocationRepository
import com.zjdx.point.data.repository.RegisterRepository
import com.zjdx.point.data.repository.TravelRepository
import com.zjdx.point.db.MyDataBase


class PointApplication : MultiDexApplication() {


    val database by lazy { MyDataBase.getDatabase(this) }
    val locationRepository by lazy { LocationRepository(database.locationDao()) }
    val travelRepository by lazy {
        TravelRepository(
            database.travelRecordDao(),
            database.locationDao()
        )
    }
    val registerRepository by lazy { RegisterRepository() }

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}