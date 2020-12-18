package com.zjdx.point.repository

import androidx.annotation.WorkerThread
import com.zjdx.point.db.dao.LocationDao
import com.zjdx.point.db.model.Location
import kotlinx.coroutines.flow.Flow

class LocationRepository(private val mLocationDao: LocationDao) {
    val locations: Flow<List<Location>> = mLocationDao.queryByTid("")

    @WorkerThread
    suspend fun insert(location: Location){
        mLocationDao.insertLocation(location)
    }
}