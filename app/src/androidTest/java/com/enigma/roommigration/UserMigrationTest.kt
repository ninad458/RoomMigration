package com.enigma.roommigration

import android.database.Cursor
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.enigma.roommigration.db.AppDatabase
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class UserMigrationTest {

    @Rule
    @JvmField
    val testHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName)

    @Test
    fun testMigration1_2() {
        val expectedElementsQueue = testHelper.addTestDataInUserVersion1()
        testHelper.migrateToVersion2AndAssert(expectedElementsQueue)
    }

    private fun MigrationTestHelper.migrateToVersion2AndAssert(expectedElementsQueue: Queue<Any>) {
        runMigrationsAndValidate(TEST_DB, 2, true, AppDatabase.MIGRATION_1_2).use { db ->
            db.query("SELECT * FROM $USER_TABLE").use cursor@{ cursor ->
                if (cursor.columnCount == 0) return@cursor
                cursor.moveToFirst()
                do {
                    if (expectedElementsQueue.isEmpty()) return
                    assertEquals(expectedElementsQueue.poll(), cursor.user)
                } while (cursor.moveToNext())
            }
        }
    }

    private fun MigrationTestHelper.addTestDataInUserVersion1(): Queue<Any> {
        createDatabase(TEST_DB, 1).use { db ->
            for (i in 0..10) {
                val name = buildString { repeat(i) { append("Hello ") } }.trim()
                db.execSQL("INSERT INTO $USER_TABLE (`uid`, `name`) VALUES ($i,'$name')")
            }
            db.close()
        }
        return LinkedList(listOf(
            Triple(0, "", ""),
            Triple(1, "Hello", ""),
            Triple(2, "Hello", "Hello"),
            Triple(3, "Hello", "Hello Hello")))
    }

    private val Cursor.user: Triple<Int, String, String>
        get() {
            val id = getInt(getColumnIndex("uid"))
            val firstName = getString(getColumnIndex("first_name"))
            val lastName = getString(getColumnIndex("last_name"))
            return Triple(id, firstName, lastName)
        }

    companion object {
        private const val TEST_DB: String = "test_db"
        private const val USER_TABLE: String = "User"
    }
}