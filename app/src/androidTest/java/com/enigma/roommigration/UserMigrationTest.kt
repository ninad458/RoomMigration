package com.enigma.roommigration

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.enigma.roommigration.db.AppDatabase
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalStdlibApi
@RunWith(AndroidJUnit4::class)
class UserMigrationTest {

    companion object {
        const val TEST_DB = "test_db"
    }

    @Rule
    @JvmField
    val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName)

    @Test
    fun migrate2_3() {
        migrationTestHelper.createDatabase(TEST_DB, 2).use {
            it.execSQL("INSERT INTO `User` (`uid`, `name`, `photo`) VALUES (1, 'Hello' , 'photo.jpg')")
        }

        migrationTestHelper.runMigrationsAndValidate(TEST_DB, 3, true, AppDatabase.MIGRATION_2_3)
            .use {
                it.query("SELECT * FROM `User`").use { cursor ->
                    assertEquals(cursor.count, 1)
                }
            }
    }

    @Test
    fun migrate3_4() {
        migrationTestHelper.createDatabase(TEST_DB, 3).use {
            it.execSQL("INSERT INTO `User` (`uid`, `name`) VALUES (0, '')")
            it.execSQL("INSERT INTO `User` (`uid`, `name`) VALUES (1, 'Hello')")
            it.execSQL("INSERT INTO `User` (`uid`, `name`) VALUES (2, 'Hello World')")
            it.execSQL("INSERT INTO `User` (`uid`, `name`) VALUES (3, 'Hello World World')")
        }

        migrationTestHelper.runMigrationsAndValidate(TEST_DB, 4, true, AppDatabase.MIGRATION_3_4)
            .use {
                it.query("SELECT * FROM `User`").use { cursor ->
                    assertEquals(cursor.count, 4)

                    cursor.moveToFirst()
                    cursor.moveToNext()
                    val firstName = cursor.getString(cursor.getColumnIndex("first_name"))
                    val lastName = cursor.getString(cursor.getColumnIndex("last_name"))
                    assertEquals(firstName, "Hello")
                    assertEquals(lastName, "")
                }
            }
    }
}