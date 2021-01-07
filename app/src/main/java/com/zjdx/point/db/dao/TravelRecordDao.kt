package com.zjdx.point.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelRecordDao {
    @Query("Select * from TravelRecord order by create_time")
    fun getAll(): List<TravelRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTravelRecord(vararg travelRecord: TravelRecord)

    @Query("Select * from TravelRecord where is_upload= 0 limit 1")
    fun findHasNotUpload(): TravelRecord

    @Query("Select count(*) from TravelRecord ")
    fun getCount(): Int

    @Query("Select count(*) from TravelRecord  where is_upload= 0")
    fun getCountNotUpload(): Int

    @Delete
    fun deleteTravelRecrod(travelRecords: TravelRecord)

    @Update
    fun updateTravelRecrod(travelRecord: TravelRecord)
}