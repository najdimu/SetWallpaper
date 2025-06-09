package com.example.setwallpaper.style_zone.style

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.setwallpaper.R
import com.example.setwallpaper.style_zone.activity.detail.PersonalStyleDetailActivity

class PersonalStyleAdapter(private var personalStyleList: MutableList<PersonalStyleItem>) : RecyclerView.Adapter<PersonalStyleAdapter.PersonalStyleViewHolder>() {


    class PersonalStyleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.style_item_image)
        val title: TextView = view.findViewById(R.id.style_item_title)
        val author: TextView = view.findViewById(R.id.style_item_author)
        val avatar: ImageView = view.findViewById(R.id.style_item_avatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonalStyleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.personal_style_item_layout, parent, false)
        return PersonalStyleViewHolder(view)
    }

    override fun getItemCount() = personalStyleList.size

    override fun onBindViewHolder(holder: PersonalStyleViewHolder, position: Int) {
        val perStyleItem = personalStyleList[position]
        holder.title.text = perStyleItem.title
        holder.author.text = perStyleItem.author
        holder.image.load(perStyleItem.mainImage){
            error(R.drawable.error_place_holder)
            placeholder(R.drawable.place_holder)
        }
        holder.avatar.load(perStyleItem.avatar) {
            error(R.drawable.error_place_holder)
            placeholder(R.drawable.place_holder)
        }
        val array: ArrayList<String> = ArrayList()
        perStyleItem.images.forEach {
            array.add(it)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PersonalStyleDetailActivity::class.java)
            intent.putExtra("title", perStyleItem.title)
            intent.putExtra("author", perStyleItem.author)
            intent.putExtra("description", perStyleItem.description)
            intent.putStringArrayListExtra("images", array)
            intent.putExtra("avatar", perStyleItem.avatar)
            intent.putExtra("themeLink", perStyleItem.themeLink)
            intent.putExtra("firstWallpaperLink", perStyleItem.firstWallpaperLink)
            intent.putExtra("secondWallpaperLink", perStyleItem.secondWallpaperLink)
            intent.putExtra("iconLink", perStyleItem.iconLink)
            intent.putExtra("fontLink", perStyleItem.fontLink)
            intent.putExtra("supportDevice", perStyleItem.supportDevice)
            holder.itemView.context.startActivity(intent)
        }

    }
}