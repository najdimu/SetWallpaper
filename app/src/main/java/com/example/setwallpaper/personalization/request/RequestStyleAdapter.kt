package com.example.setwallpaper.personalization.request

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.setwallpaper.R

class RequestStyleAdapter(private var requestList: MutableList<RequestStyleItem>): RecyclerView.Adapter<RequestStyleAdapter.RequestStyleViewHolder>() {

    class RequestStyleViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.request_style_item_title)
        val avatar: ImageView = view.findViewById(R.id.request_style_item_avatar)
        val author: TextView = view.findViewById(R.id.request_style_item_author)
        val status: TextView = view.findViewById(R.id.request_style_item_status)
        val date: TextView = view.findViewById(R.id.request_style_item_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestStyleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.request_style_item_layout, parent, false)
        return RequestStyleViewHolder(view)
    }

    override fun getItemCount() = requestList.size

    override fun onBindViewHolder(holder: RequestStyleViewHolder, position: Int) {
        val request = requestList[position]
        holder.title.text = request.title
        holder.author.text = request.author
        holder.date.text = request.date
        holder.avatar.load(request.avatar){
            error(R.drawable.error_place_holder)
            placeholder(R.drawable.place_holder)
        }
        if (request.status){
            holder.status.text = "getString(R.string.success)"
        } else{
            holder.status.text = "getString(R.string.error)"
        }
    }
}