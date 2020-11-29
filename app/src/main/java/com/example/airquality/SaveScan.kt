package com.example.airquality

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_scan.*

class SaveScan : AppCompatActivity() {
    lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        val AQI = intent.getStringExtra("AQI")!!
        val date = intent.getStringExtra("date")!!
        val city = intent.getStringExtra("city")!!
        air_quality.text = AQI
        nearest_city.text = city
        save.setOnClickListener {
            database = FirebaseDatabase.getInstance().reference
            saveScan(AQI, city, date)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        ignore.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveScan (AQI : String, city : String , date : String) {
        val search = AQSearch(AQI, city, date)
        val key = database.child("search").push().key!!
        search.uuid = key
        Log.e("", "strangeness " + search.aq)
        database.child("search").child(key).setValue(search)
    }
}