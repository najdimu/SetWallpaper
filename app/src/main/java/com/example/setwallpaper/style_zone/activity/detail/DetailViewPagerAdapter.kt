package com.example.setwallpaper.style_zone.activity.detail

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.example.setwallpaper.R

class DetailViewPagerAdapter(private val images: List<String>) : RecyclerView.Adapter<DetailViewPagerAdapter.DetailViewHolder>() {
    inner class DetailViewHolder(val imageView: ImageView): ViewHolder(imageView)

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): DetailViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        return DetailViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: DetailViewPagerAdapter.DetailViewHolder, position: Int) {
        holder.imageView.load(images[position]) {
            error(R.drawable.error_place_holder)
        }
    }

    override fun getItemCount(): Int = images.size
}