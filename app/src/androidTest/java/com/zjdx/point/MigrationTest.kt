package com.zjdx.point;

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zjdx.point.db.MyDataBase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "Point"

    @Rule
    fun helper(): MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MyDataBase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        var db = helper().createDatabase(TEST_DB, 2).apply {
            // db has schema version 1. insert some data using SQL queries.
            // You cannot use DAO classes because they expect the latest schema.

//           execSQL("Insert Into Location values('bdfa0840-79a9-45c0-8917-d6cd95209188','62c5bbee-e683-4ec0-90c3-31554927898f','34.79235507471677'," +
//                   "'113.59934812834608','0.0',''在河南中天航空大厦附近','140.55','9.476947784423828','1','河南省郑州市中原区外方路269号靠近河南中天航空大厦'," +
//                   "'2020-12-31 15:30:18')")

            // Prepare for the next version.
            close()
        }

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        db = helper().runMigrationsAndValidate(TEST_DB, 3, true, MyDataBase.MIGRATION_2_3)

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
    }

    @Test
    @Throws(IOException::class)
    fun migrate3To4() {
        var db = helper().createDatabase(TEST_DB, 3).apply {
            close()
        }

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        db = helper().runMigrationsAndValidate(TEST_DB, 4, true, MyDataBase.MIGRATION_3_4)

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
    }
}
