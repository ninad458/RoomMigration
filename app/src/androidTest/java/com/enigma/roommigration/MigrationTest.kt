package com.enigma.roommigration

import android.database.Cursor
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.enigma.roommigration.db.AppDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Rule
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
abstract class MigrationTest {

    @Rule
    @JvmField
    val testHelper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory())

    protected fun createDatabase(version: Int): SupportSQLiteDatabase {
        return testHelper.createDatabase(TEST_DB_NAME, version)
    }

    protected fun runMigrations(version: Int, vararg migration: Migration): SupportSQLiteDatabase {
        return testHelper.runMigrationsAndValidate(TEST_DB_NAME, version, true, *migration)
    }

    protected fun Cursor.assertCursorNotEmpty() {
        assertNotNull(this)
        assertEquals(moveToFirst(), true)
    }

    companion object {
        private const val TEST_DB_NAME = "test_db"

    }
}