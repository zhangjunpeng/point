package com.zjdx.point.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.zjdx.point.db.model.Location

@Dao
interface LocationDao {
    @Query("Select * from Location")
    fun getAll(): List<Location>

    @Query("Select * from Location where t_id = :tId")
    fun findByTid(tId: String): Location
}