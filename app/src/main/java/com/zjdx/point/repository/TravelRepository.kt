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
    fun getAll(): List<TravelRecord> {
        return travelRecordDao.getAll()
    }


    @WorkerThread
    fun getLocationListById(tid: String): MutableList<Location> {
        return locationDao.queryByTid(tid).toMutableList()
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
    fun findHasNotUpload(): TravelRecord {
        return travelRecordDao.findHasNotUpload()
    }

    @WorkerThread
    fun getCount():Int{
        return travelRecordDao.getCount()
    }

    @WorkerThread
    fun getCountNotUpload():Int{
        return travelRecordDao.getCountNotUpload()
    }

    @WorkerThread
    fun updateLocations(locations: List<Location>){
        locationDao.updateLocations(locations)
    }

    @WorkerThread
    fun getLocationsHasNotUpload(tid:String): MutableList<Location>{
        return locationDao.queryListHasNotUploadByTid(tid).toMutableList()
    }
}