package com.zjdx.point.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.zjdx.point.db.model.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("Select * from Location")
    fun getAll(): Flow<List<Location>>

    @Query("Select * from Location where t_id = :tId order by creat_time")
    fun queryByTid(tId: String): Array<Location>

    @Query("Select * from Location where t_id = :tId and is_upload=0 order by creat_time desc limit 1000")
    fun queryListHasNotUploadByTid(tId: String): Array<Location>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: Location)

    @Update
    fun updateLocations(locations: List<Location>)
}