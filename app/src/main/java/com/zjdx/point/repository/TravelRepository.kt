package com.zjdx.point.repository

import androidx.annotation.WorkerThread
import com.zjdx.point.db.dao.LocationDao
import com.zjdx.point.db.dao.TravelRecordDao
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import kotlinx.coroutines.flow.Flow

class TravelRepository(
    private val travelRecordDao: TravelRecordDao,
    private val locationDao: LocationDao
) {

    @WorkerThread
    fun getAll(): Flow<List<TravelRecord>> {
        return travelRecordDao.getAll()
    }

    @WorkerThread
    fun getLocationById(tid: String): Flow<List<Location>> {
        return locationDao.queryByTid(tid)
    }

    @WorkerThread
    fun getLocationListById(tid: String): List<Location> {
        return locationDao.queryListByTid(tid)
    }

    @WorkerThread
    fun insertTravelRecord(travelRecord: TravelRecord) {
        travelRecordDao.insertTravelRecord(travelRecord)
    }

    @WorkerThread
    fun insertLocation(location: Location) {
        locationDao.insertLocation(location)
    }

    @WorkerThread
    fun findHasNotUpload():TravelRecord{
        return travelRecordDao.findHasNotUpload()
    }

}