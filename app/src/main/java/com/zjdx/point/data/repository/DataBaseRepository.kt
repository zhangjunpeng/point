package com.zjdx.point.data.repository

import androidx.annotation.WorkerThread
import com.zjdx.point.data.DataSource
import com.zjdx.point.data.bean.AppVersionModel
import com.zjdx.point.data.bean.Back
import com.zjdx.point.db.dao.LocationDao
import com.zjdx.point.db.dao.TagRecordDao
import com.zjdx.point.db.dao.TravelRecordDao
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TagRecord
import com.zjdx.point.db.model.TravelRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataBaseRepository(
    private val travelRecordDao: TravelRecordDao,
    private val locationDao: LocationDao,
    private val tagRecordDao: TagRecordDao,
) {

    val dataSource = DataSource()

    @WorkerThread
    suspend fun getAll(uid: String, startTime: Long, endTime: Long): List<TravelRecord> {
        return withContext(Dispatchers.IO) {
            travelRecordDao.getAll(uid, startTime, endTime)
        }
    }
    @WorkerThread
    fun getAllTravel(uid: String, startTime: Long, endTime: Long): List<TravelRecord> {
        return travelRecordDao.getAll(uid, startTime, endTime)
    }

    @WorkerThread
     fun getLocationListById(tid: String): MutableList<Location> {
        return locationDao.queryByTid(tid).toMutableList()

    }

    @WorkerThread
    fun getLocationListByTime(startTime: String,endTime: String): MutableList<Location> {
        return locationDao.getLastLocationByTime(startTime,endTime).toMutableList()
    }

    @WorkerThread
    fun getLocationListGroupByTId(): MutableList<Location> {
        return locationDao.getLocationListGroupByTId().toMutableList()

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
    fun getTravelRecordHasNotUpload(uid: String): List<TravelRecord> {
        return travelRecordDao.getTravelRecordHasNotUpload(uid)
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

    @WorkerThread
    fun getNotUpload(): MutableList<TagRecord> {
        return tagRecordDao.queryAllTagtHasNotUploadByTid().toMutableList()
    }

    @WorkerThread
    fun insertTag(tagRecord: TagRecord) {
        return tagRecordDao.insertTagRecord(tagRecord)
    }

    suspend fun getAppVersion(): Back<AppVersionModel> {
        return dataSource.getAppVersion()
    }

    @WorkerThread
    suspend fun getTravelList(paramMap: Map<String, String>):MutableList<TravelRecord>{
        return  dataSource.getTravelListByTime(paramMap)
    }
}