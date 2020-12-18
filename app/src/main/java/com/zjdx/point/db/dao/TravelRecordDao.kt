package com.zjdx.point.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.zjdx.point.db.model.TravelRecord

@Dao
interface TravelRecordDao {
    @Query("Select * from TravelRecord")
    fun getAll(): LiveData<List<TravelRecord>>
}