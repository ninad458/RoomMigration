package com.enigma.roommigration.db

import android.content.Context
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [User::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    companion object {
        @Synchronized
        fun getInstance(context: Context): AppDatabase = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "migration-db"
        ).addMigrations(*MIGRATION_SCRIPTS).build()

        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(database: SupportSQLiteDatabase) {
                val names = getNames(database)
                createTempUsersTable(database)
                insertDataInTempTable(database, names)
                renameTempUsersTable(database)
            }

            private fun getNames(database: SupportSQLiteDatabase): List<Triple<Int, String, String>> {
                return database.query("SELECT * FROM user").use { cursor ->
                    if (cursor.count == 0) return@use listOf()
                    cursor.moveToFirst()
                    return@use buildList {
                        while (cursor.moveToNext()) {
                            val nameIndex = cursor.getColumnIndexOrThrow("name")
                            val name = cursor.getStringOrNull(nameIndex).orEmpty()
                            val names = name.split(" ")
                            val firstName = names.getOrNull(0).orEmpty()
                            val lastName = names.drop(1).joinToString(" ")
                            val idIndex = cursor.getColumnIndex("uid")
                            val id = cursor.getIntOrNull(idIndex) ?: continue

                            add(Triple(id, firstName, lastName))
                        }
                    }
                }
            }

            private fun insertDataInTempTable(
                database: SupportSQLiteDatabase,
                names: List<Triple<Int, String, String>>,
            ) {
                for ((uid, firstName, lastName) in names) {
                    database.execSQL("""INSERT INTO `user_temp` 
                                        (`uid`, `first_name`, `last_name`) 
                                        VALUES 
                                        ($uid, "$firstName", "$lastName")""")
                }
            }

            private fun renameTempUsersTable(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE `user`")
                database.execSQL("ALTER TABLE `user_temp` RENAME TO `user`")
            }

            private fun createTempUsersTable(database: SupportSQLiteDatabase) {
                database.execSQL("""CREATE TABLE IF NOT EXISTS `user_temp` 
                                    (`uid` INTEGER NOT NULL, 
                                    `first_name` TEXT, 
                                    `last_name` TEXT, 
                                    PRIMARY KEY(`uid`))""")
            }
        }

        private val MIGRATION_SCRIPTS = arrayOf<Migration>(MIGRATION_1_2)

    }
}