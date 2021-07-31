package com.enigma.roommigration.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [User::class], version = 3, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    companion object {
        @Synchronized
        fun getInstance(context: Context): AppDatabase = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "migration-db"
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_0_2).build()


        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE User ADD COLUMN photo TEXT")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // create a temp user table
                database.execSQL("""CREATE TABLE IF NOT EXISTS `temp_user` 
                                    (`uid` INTEGER NOT NULL, `name` TEXT, PRIMARY KEY(`uid`))""")
                // populate data in temp table
                database.execSQL("""INSERT INTO `temp_user` (`uid`, `name`) SELECT `uid`, `name` FROM `User`""")
                // drop the original table
                database.execSQL("DROP TABLE `User`")
                // rename temp table to user
                database.execSQL("ALTER TABLE `temp_user` RENAME TO `User`")
            }
        }

        val MIGRATION_0_2 = object : Migration(0, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }
    }
}