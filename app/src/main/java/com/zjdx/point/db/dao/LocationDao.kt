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

    @Query("Select * from Location where t_id = :tId order by creat_time desc limit 1")
    fun getLastLocationById(tId: String): Location

    @Query("Select * from Location where t_id = :tId and is_upload=0 order by creat_time desc limit 1000")
    fun queryListHasNotUploadByTid(tId: String): Array<Location>

    @Query("Select * from Location where is_upload=0  limit 1")
    fun queryOneHasNotUploadByTid(): Location

    @Query("Select * from Location where t_id = :tId and is_upload=0")
    fun queryAllListHasNotUploadByTid(tId: String): Array<Location>

//    @Query("Delete * from Location where t_id= :tid")
//    fun deleteLocationsByTid(tId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: Location)

    @Update
    fun updateLocations(locations: List<Location>)

    @Delete
    fun deleteLocations(locations: List<Location>)
}