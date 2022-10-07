package com.zjdx.point.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zjdx.point.db.dao.LocationDao
import com.zjdx.point.db.dao.TagRecordDao
import com.zjdx.point.db.dao.TravelRecordDao
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TagRecord
import com.zjdx.point.db.model.TravelRecord

@Database(
    entities = [Location::class, TravelRecord::class, TagRecord::class],
    version = 7,
    exportSchema = true
)
abstract class MyDataBase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun travelRecordDao(): TravelRecordDao

    abstract fun tagRecordDao(): TagRecordDao
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: MyDataBase? = null

        fun getDatabase(context: Context): MyDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, MyDataBase::class.java, "Point"
                ).allowMainThreadQueries().addMigrations(
                    MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6,
                    MIGRATION_6_7
                )
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(" ALTER TABLE Location RENAME TO Location_temp_old ")
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `Location` (`lid` TEXT NOT NULL, `t_id` TEXT NOT NULL, `lat` REAL NOT NULL, `lng` REAL NOT NULL, `speed` REAL NOT NULL, `direction` TEXT NOT NULL, `altitude` REAL NOT NULL, `accuracy` REAL NOT NULL, `source` TEXT NOT NULL, `address` TEXT NOT NULL, `is_upload` INTEGER NOT NULL DEFAULT 0, `creat_time` TEXT NOT NULL, PRIMARY KEY(`lid`))"
                )
                database.execSQL(
                    "INSERT INTO Location (lid,t_id,lat,lng,speed,direction,altitude,accuracy,source,address,creat_time) SELECT " +
                            " lid,t_id,lat,lng,speed,direction,altitude,accuracy,source,address,creat_time " +
                            " FROM Location_temp_old"
                )
//                database.execSQL("drop TABLE Location_temp_old")

            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE TravelRecord ADD COLUMN start_time INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE TravelRecord ADD COLUMN end_time INTEGER NOT NULL DEFAULT 0")
            }
        }


        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Location ADD COLUMN MCC INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE Location ADD COLUMN MNC INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE Location ADD COLUMN LAC INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE Location ADD COLUMN CID INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE Location ADD COLUMN BSSS INTEGER NOT NULL DEFAULT 0")
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `TagRecord` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `destination` TEXT NOT NULL, `desc` TEXT NOT NULL, `start_time` TEXT NOT NULL, `end_time` TEXT NOT NULL, `start_type` TEXT NOT NULL, `end_type` TEXT NOT NULL, `travel_model` TEXT NOT NULL, `isupload` INTEGER NOT NULL DEFAULT 0)"
                )
            }
        }
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE TagRecord ADD COLUMN upload_date TEXT NOT NULL DEFAULT '0'")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL("ALTER TABLE TagRecord ADD COLUMN create_time TEXT NOT NULL DEFAULT '0'")
            }
        }

    }


}