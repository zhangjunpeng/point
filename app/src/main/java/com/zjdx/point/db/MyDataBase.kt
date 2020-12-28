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
                            "\t\"lid\"  TEXT NOT NULL PRIMARY KEY DEFAULT 0,\n" +
                            "\t\"t_id\" TEXT ,\n" +
                            "\t\"lat\" TEXT ,\n" +
                            "\t\"lng\" TEXT ,\n" +
                            "\t\"speed\" TEXT,\n" +
                            "\t\"direction\" TEXT ,\n" +
                            "\t\"altitude\" TEXT,\n" +
                            "\t\"accuracy\" TEXT ,\n" +
                            "\t\"source\" TEXT ,\n" +
                            "\t\"address\" TEXT ,\n" +
                            "\t\"creat_time\" TEXT \n" +
                            ")"
                )
                database.execSQL(
                    "INSERT INTO Location SELECT\n" +
                            " * \n" +
                            "FROM\n " +
                            " Location_temp_old;"
                )
                database.execSQL("drop TABLE Location_temp_old")

            }
        }


    }


}