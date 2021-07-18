package com.enigma.roommigration.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    companion object {
        @Synchronized
        fun getInstance(context: Context): AppDatabase = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "migration-db"
        ).build()
    }
}