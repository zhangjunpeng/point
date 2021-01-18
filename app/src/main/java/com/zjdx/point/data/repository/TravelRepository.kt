package com.zjdx.point.data.repository

import androidx.annotation.WorkerThread
import com.zjdx.point.data.DataSource
import com.zjdx.point.data.bean.AppVersionModel
import com.zjdx.point.data.bean.Back
import com.zjdx.point.db.dao.LocationDao
import com.zjdx.point.db.dao.TravelRecordDao
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TravelRepository(
    private val travelRecordDao: TravelRecordDao,
    private val locationDao: LocationDao
) {

    val dataSource = DataSource()

    @WorkerThread
    suspend fun getAll(uid: String, startTime: Long, endTime: Long): List<TravelRecord> {
        return withContext(Dispatchers.IO) {
            travelRecordDao.getAll(uid, startTime, endTime)
        }
    }

    @WorkerThread
     fun getLocationListById(tid: String): MutableList<Location> {
        return locationDao.queryByTid(tid).toMutableList()

    }

    @WorkerThread
    fun queryOneHasNotUploadByTid(): Location {
        return locationDao.queryOneHasNotUploadByTid()
    }

    @WorkerThread
     fun getLastLocationById(tid: String): Location {
        return locationDao.getLastLocationById(tid)

    }
    @WorkerThread
    fun getFirsttLocationById(tid: String): Location {
        return locationDao.getFirsttLocationById(tid)

    }

    @WorkerThread
    fun getTravelRecordById(tid: String): TravelRecord {
        return travelRecordDao.getTravelRecordById(tid)
    }
    @WorkerThread
    fun getTravelRecordHasEmptyStart():  List<TravelRecord> {
        return travelRecordDao.getTravelRecordHasEmptyStart()
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
    fun updateTravelRecord(travelRecord: TravelRecord) {
        travelRecordDao.updateTravelRecrod(travelRecord)
    }

    @WorkerThread
    fun getLocationsHasNotUpload(tid: String): MutableList<Location> {
        return locationDao.queryListHasNotUploadByTid(tid).toMutableList()
    }

    @WorkerThread
    fun getAllListHasNotUploadByTid(tid: String): MutableList<Location> {
        return locationDao.queryAllListHasNotUploadByTid(tid).toMutableList()

    }

    @WorkerThread
    fun deteleTravel(travelRecords: TravelRecord) {
        travelRecordDao.deleteTravelRecrod(travelRecords)
    }

    @WorkerThread
    fun deteleLocation(Locations: List<Location>) {
        locationDao.deleteLocations(Locations)
    }


    suspend fun getAppVersion(): Back<AppVersionModel> {
        return dataSource.getAppVersion()
    }

}