package com.zjdx.point.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.zjdx.point.db.model.TravelRecord

@Dao
interface TravelRecordDao {
    @Query("Select * from travelrecord")
    fun getAll(): List<TravelRecord>
}