package com.example.airquality

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_scan.*

class SaveScan : AppCompatActivity() {
    lateinit var storage : StorageReference
    lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        var AQI = intent.getStringExtra("AQI")!!
        var date = intent.getStringExtra("date")!!
        var city = intent.getStringExtra("city")!!
        air_quality.text = AQI
        save.setOnClickListener {
            saveScan(AQI, date)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        ignore.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun saveScan (AQI : String, date : String) {
        //firebase here
    }
}