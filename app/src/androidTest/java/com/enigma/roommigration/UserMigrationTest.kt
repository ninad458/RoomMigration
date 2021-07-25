package com.enigma.roommigration

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.enigma.roommigration.db.AppDatabase
import junit.framework.TestCase.assertEquals
import org.junit.Test


class UserMigrationTest : MigrationTest() {

    companion object {
        private const val USER_TABLE_NAME = "User"
    }

    @Test
    fun migrate1To2() {
        val userName = "Loki"
        val uid = 1
        createDatabase(1).use { db -> db.insertUser(uid, userName) }

        runMigrations(2, AppDatabase.MIGRATION_1_2).use { db ->
            db.getAllUsers().use { cursor ->
                cursor.assertCursorNotEmpty()
                assertUserEquals(cursor, userName, uid)
            }
        }
    }

    private fun assertUserEquals(
        cursor: Cursor,
        userName: String,
        uid: Int,
    ) {
        val name = cursor.getString(cursor.getColumnIndex("name"))
        val id = cursor.getInt(cursor.getColumnIndex("uid"))
        val photo = cursor.getString(cursor.getColumnIndex("photo"))

        assertEquals(name, userName)
        assertEquals(id, uid)
        assertEquals(photo, null)
    }

    private fun SupportSQLiteDatabase.getAllUsers() = query("SELECT * FROM $USER_TABLE_NAME")

    private fun SupportSQLiteDatabase.insertUser(uid: Int, userName: String): Long {
        val values = ContentValues()
        values.put("uid", uid)
        values.put("name", userName)
        return insert(USER_TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, values)
    }
}