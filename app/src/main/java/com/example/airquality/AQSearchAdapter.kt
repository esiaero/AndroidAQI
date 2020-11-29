package com.example.airquality

import android.app.Activity
import android.net.Uri
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.search.view.*

class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var city: TextView = view.city
    var date: TextView = view.date
    var aq: TextView = view.aq
    var delete: ImageButton = view.delete
    var id : String = ""

    fun bind(item: AQSearch) {
        city.text = item.city
        date.text = item.date
        aq.text = item.aq
        id = item.uuid
    }
}

class SearchAdapter(private var activity: Activity, private var feed: MutableList<AQSearch>)
    : RecyclerView.Adapter<SearchViewHolder>() {
    private val tag = "AQSearchAdapter"

    private lateinit var database : DatabaseReference

    private fun removeAt(holder: SearchViewHolder) {
        val position = holder.adapterPosition
        feed.removeAt(position)
        val search = database.child("search").child(holder.id)
        search.removeValue().addOnFailureListener {
            Log.e(tag, "bad things happened in realtime database :(")
        }
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, feed.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val holder = SearchViewHolder(LayoutInflater.from(activity).inflate(R.layout.search, parent, false))
        database = FirebaseDatabase.getInstance().reference

        holder.delete.setOnClickListener {
            removeAt(holder)
        }
        return holder
    }
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(feed[position])
    }

    override fun getItemCount(): Int = feed.size
}