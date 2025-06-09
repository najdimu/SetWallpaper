package com.example.setwallpaper.style_zone.activity.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.example.setwallpaper.R
import com.google.android.material.imageview.ShapeableImageView

class DetailViewPagerAdapter(private val images: List<String>) : RecyclerView.Adapter<DetailViewPagerAdapter.DetailViewHolder>() {
    inner class DetailViewHolder(val view: View): ViewHolder(view){
        val imageView: ShapeableImageView = view.findViewById(R.id.image_view_detail_layout)
    }

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.detail_image_layout, parent, false)
        return DetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailViewPagerAdapter.DetailViewHolder, position: Int) {
        holder.imageView.load(images[position]) {
            error(R.drawable.error_place_holder)
        }
    }

    override fun getItemCount(): Int = images.size
}