package com.enigma.roommigration.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Insert
    fun insertAll(vararg users: User)

}