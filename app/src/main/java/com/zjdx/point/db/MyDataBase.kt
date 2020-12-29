package com.zjdx.point.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zjdx.point.db.dao.LocationDao
import com.zjdx.point.db.dao.TravelRecordDao
import com.zjdx.point.db.model.Location
import com.zjdx.point.db.model.TravelRecord

@Database(
    entities = [Location::class, TravelRecord::class],
    version = 3,
    exportSchema = true
)
abstract class MyDataBase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun travelRecordDao(): TravelRecordDao

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
                    context.applicationContext,
                    MyDataBase::class.java,
                    "Point"
                ).allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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
                    "CREATE TABLE IF NOT EXISTS Location(\n" +
                            "\tlid  TEXT NOT NULL PRIMARY KEY DEFAULT 0,\n" +
                            "\tt_id TEXT ,\n" +
                            "\tlat TEXT ,\n" +
                            "\tlng TEXT ,\n" +
                            "\tspeed TEXT,\n" +
                            "\tdirection TEXT ,\n" +
                            "\taltitude TEXT,\n" +
                            "\taccuracy TEXT ,\n" +
                            "\tsource TEXT ,\n" +
                            "\taddress TEXT ,\n" +
                            "\tcreat_time TEXT \n" +
                            ")"
                )
                database.execSQL(
                    "INSERT INTO Location (lid,t_id,lat,lng,speed,direction,altitude,accuracy,address,creat_time) SELECT\n" +
                            " lid,t_id,lat,lng,speed,direction,altitude,accuracy,address,creat_time\n" +
                            "FROM\n " +
                            " Location_temp_old;"
                )
//                database.execSQL("drop TABLE Location_temp_old")

            }
        }


    }


}