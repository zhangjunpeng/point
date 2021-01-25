package com.zjdx.point.data.repository

import androidx.annotation.WorkerThread
import com.zjdx.point.db.dao.LocationDao
import com.zjdx.point.db.model.Location

class LocationRepository(private val mLocationDao: LocationDao){

    @WorkerThread
    fun getlocations(): Array<Location> {
        return mLocationDao.queryByTid("")
    }

    @WorkerThread
    suspend fun insert(location: Location) {
        mLocationDao.insertLocation(location)
    }


}