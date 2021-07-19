package com.enigma.roommigration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.enigma.roommigration.db.AppDatabase
import com.enigma.roommigration.db.User

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val userDao = AppDatabase.getInstance(this).getUserDao()
        Thread {
            userDao.insertAll(
                User(1, "Baburao Ganpatrao Apte"),
                User(2, "Devi Prasad"),
                User(3, "Raju"))
        }.start()
    }
}