package com.zjdx.point.db.dao

import androidx.room.*
import com.zjdx.point.db.model.TagRecord

@Dao
interface TagRecordDao {

    @Query("Select * from TagRecord where  isupload=0 order by sort ")
    fun queryAllTagtHasNotUploadByTid(): Array<TagRecord>

    @Query("Select * from TagRecord where  isupload=1")
    fun queryAllTagtHasUpload(): Array<TagRecord>

    @Query("Select * from TagRecord where  isupload=1 and upload_date between :startTime and :endTime ")
    fun queryAllTagtHasUploadbyTime(startTime: String, endTime: String): Array<TagRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTagRecord(tagRecord: TagRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTagRecords(tagRecord: MutableList<TagRecord>)

    @Update
    fun updateTagRecord(tagRecords: List<TagRecord>)

    @Update
    fun updateTag(tagRecord: TagRecord)

    @Delete
    fun delDBTag(tagRecords: List<TagRecord>)

}