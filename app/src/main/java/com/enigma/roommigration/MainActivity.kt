package com.enigma.roommigration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.enigma.roommigration.db.AppDatabase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val userDao = AppDatabase.getInstance(this).getUserDao()
        Thread {
            userDao.getAll()
        }.start()
    }
}