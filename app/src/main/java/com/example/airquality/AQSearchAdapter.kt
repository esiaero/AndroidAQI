package com.example.airquality

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.search.view.*

class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var location: TextView = view.location
    var date: TextView = view.date
    var id : String = ""

    fun bind(item: AQSearch) {
        location.text = item.location
        date.text = item.date
        id = item.uuid
    }
}

class PostAdapter(private var activity: Activity, private var feed: MutableList<AQSearch>)
    : RecyclerView.Adapter<PostViewHolder>() {

    private lateinit var storage : FirebaseStorage
    private lateinit var database : DatabaseReference
    private val POSTS = "posts"

    private fun removeAt(holder: PostViewHolder) {
        val position = holder.adapterPosition

        feed.removeAt(position)

        val pic = database.child(POSTS).child(holder.id)
        pic.removeValue().addOnFailureListener {
            Log.e("error", "bad things happened in realtime database :(")
        }
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, feed.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val holder = PostViewHolder(LayoutInflater.from(activity).inflate(R.layout.search, parent, false))
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance().reference

//        holder.delete.setOnClickListener {
//            removeAt(holder)
//        }
        return holder
    }
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(feed[position])
    }

    override fun getItemCount(): Int = feed.size
}