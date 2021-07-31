package com.enigma.roommigration.db

import android.content.Context
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
        ).addMigrations(MIGRATION_1_2).build()


        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE User ADD COLUMN photo TEXT")
            }
        }
    }
}