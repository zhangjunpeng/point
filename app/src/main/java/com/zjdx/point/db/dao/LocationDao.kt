package com.zjdx.point.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zjdx.point.db.model.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("Select * from Location")
    fun getAll(): List<Location>

    @Query("Select * from Location where t_id = :tId")
    fun queryByTid(tId: String): Flow<List<Location>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(vararg location: Location)
}