package com.zjdx.point

import android.app.Application
import com.zjdx.point.db.MyDataBase
import com.zjdx.point.data.repository.LocationRepository
import com.zjdx.point.data.repository.TravelRepository

class PointApplication : Application() {


    val database by lazy { MyDataBase.getDatabase(this) }
    val locationRepository by lazy { LocationRepository(database.locationDao()) }
    val travelRepository by lazy {
        TravelRepository(
            database.travelRecordDao(),
            database.locationDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
    }
}