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

    @Test
    fun testMigration2_3() {
        val expectedElementsQueue = testHelper.addTestDataInUserVersion2For3()
        testHelper.migrateToVersion3AndAssert(expectedElementsQueue)
    }

    private fun MigrationTestHelper.migrateToVersion2AndAssert(expectedElementsQueue: Queue<Any>) {
        runMigrationsAndValidate(TEST_DB, 2, true, AppDatabase.MIGRATION_1_2).use { db ->
            db.query("SELECT * FROM $USER_TABLE").use cursor@{ cursor ->
                if (cursor.columnCount == 0) return@cursor
                cursor.moveToFirst()
                do {
                    if (expectedElementsQueue.isEmpty()) return
                    assertEquals(expectedElementsQueue.poll(), cursor.user2)
                } while (cursor.moveToNext())
            }
        }
    }

    private fun MigrationTestHelper.migrateToVersion3AndAssert(expectedElementsQueue: Queue<Any>) {
        runMigrationsAndValidate(TEST_DB, 3, true, AppDatabase.MIGRATION_2_3).use { db ->
            db.query("SELECT * FROM $USER_TABLE").use cursor@{ cursor ->
                if (cursor.columnCount == 0) return@cursor
                cursor.moveToFirst()
                do {
                    if (expectedElementsQueue.isEmpty()) return
                    assertEquals(expectedElementsQueue.poll(), cursor.user3)
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

    private fun MigrationTestHelper.addTestDataInUserVersion2For3(): Queue<Any> {
        createDatabase(TEST_DB, 2).use { db ->
            for (i in 0..10) {
                val firstName = "Hello $i"
                val lastName = "World $i"
                db.execSQL("""INSERT INTO $USER_TABLE (`uid`, `first_name`, `last_name`) 
                                VALUES ($i,'$firstName', '$lastName')""")
            }
            db.close()
        }
        return LinkedList(listOf(
            Triple(0, "Hello 0", "World 0"),
            Triple(1, "Hello 1", "World 1"),
            Triple(2, "Hello 2", "World 2"),
            Triple(3, "Hello 3", "World 3")))
    }

    private val Cursor.user2: Triple<Int, String, String>
        get() {
            val id = getInt(getColumnIndex("uid"))
            val firstName = getString(getColumnIndex("first_name"))
            val lastName = getString(getColumnIndex("last_name"))
            return Triple(id, firstName, lastName)
        }

    private val Cursor.user3: Triple<Int, String, String>
        get() {
            val id = getInt(getColumnIndex("roll_no"))
            val firstName = getString(getColumnIndex("first_name"))
            val lastName = getString(getColumnIndex("last_name"))
            return Triple(id, firstName, lastName)
        }

    companion object {
        private const val TEST_DB: String = "test_db"
        private const val USER_TABLE: String = "User"
    }
}