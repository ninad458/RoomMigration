package com.enigma.roommigration

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.enigma.roommigration.db.AppDatabase
import com.enigma.roommigration.db.User

@ExperimentalStdlibApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val userDao = AppDatabase.getInstance(this).getUserDao()
        Thread {
//            userDao.insertAll(
//                User(1, "Baburao Ganpatrao Apte"),
//                User(2, "Devi Prasad"),
//                User(3, "Raju"))
            // todo try main thread query
            findViewById<TextView>(R.id.tv_data).text = userDao.getAll().joinToString()
        }.start()
    }
}