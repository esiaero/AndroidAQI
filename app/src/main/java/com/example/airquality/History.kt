package com.example.airquality

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.search_history.*

class History : AppCompatActivity() {
    private var historyIds: MutableList<String> = ArrayList()
    private var history: MutableList<AQSearch> = ArrayList()
    lateinit var database : DatabaseReference
    private val tag = "History"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_history)
        database = FirebaseDatabase.getInstance().reference
        searches.layoutManager = LinearLayoutManager(this)
        searches.adapter = SearchAdapter(this, history)
        loadHistory()
    }

    private fun loadHistory() {
        database.child("search").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot : DataSnapshot) {
                dataSnapshot.children.forEach{
                    val search = it.getValue<AQSearch>(AQSearch::class.java)
                    if (!historyIds.contains(search!!.uuid)) {
                        historyIds.add(search.uuid)
                        history.add(search)
                    }
                }
                searches.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error : DatabaseError) {
                Log.e(tag, "Error occurred loading history", error.toException())
            }
        })
    }

}