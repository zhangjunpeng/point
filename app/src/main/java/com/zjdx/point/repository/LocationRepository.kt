package com.zjdx.point.repository

import androidx.annotation.WorkerThread
import com.zjdx.point.db.dao.LocationDao
import com.zjdx.point.db.model.Location
import kotlinx.coroutines.flow.Flow

class LocationRepository(private val mLocationDao: LocationDao) :Repository(){

    @WorkerThread
    fun getlocations(): Flow<List<Location>> {
        return mLocationDao.queryByTid("")
    }

    @WorkerThread
    suspend fun insert(location: Location) {
        mLocationDao.insertLocation(location)
    }


}