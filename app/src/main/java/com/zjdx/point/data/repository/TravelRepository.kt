package com.zjdx.point.data.repository

import androidx.annotation.WorkerThread
import com.zjdx.point.data.DataSource
import com.zjdx.point.data.bean.AppVersionModel
import com.zjdx.point.data.bean.Back
import com.zjdx.point.db.dao.LocationDao
import com.zjdx.point.db.dao.TravelRecordDao
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord

class TravelRepository(
    private val travelRecordDao: TravelRecordDao,
    private val locationDao: LocationDao
) {

    val dataSource = DataSource()

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
    fun getCount(): Int {
        return travelRecordDao.getCount()
    }

    @WorkerThread
    fun getCountNotUpload(): Int {
        return travelRecordDao.getCountNotUpload()
    }

    @WorkerThread
    fun updateLocations(locations: List<Location>) {
        locationDao.updateLocations(locations)
    }

    @WorkerThread
    fun getLocationsHasNotUpload(tid: String): MutableList<Location> {
        return locationDao.queryListHasNotUploadByTid(tid).toMutableList()
    }

    suspend fun getAppVersion(): Back<AppVersionModel> {

        return dataSource.getAppVersion()
    }

}