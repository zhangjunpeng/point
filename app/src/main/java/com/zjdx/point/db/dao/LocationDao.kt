package com.zjdx.point.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zjdx.point.db.model.Location

@Dao
interface LocationDao {
    @Query("Select * from Location")
    fun getAll(): List<Location>

    @Query("Select * from Location where t_id = :tId")
    fun queryByTid(tId: String): LiveData<List<Location>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(vararg location: Location)
}