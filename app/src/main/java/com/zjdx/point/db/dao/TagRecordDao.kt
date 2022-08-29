package com.zjdx.point.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zjdx.point.db.model.TagRecord

@Dao
interface TagRecordDao {

    @Query("Select * from TagRecord where  isupload=0")
    fun queryAllTagtHasNotUploadByTid(): Array<TagRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTagRecord(tagRecord: TagRecord)

}