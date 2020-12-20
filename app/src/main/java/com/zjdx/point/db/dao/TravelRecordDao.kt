package com.zjdx.point.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelRecordDao {
    @Query("Select * from TravelRecord")
    fun getAll(): Flow<List<TravelRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTravelRecord(vararg travelRecord: TravelRecord)
}