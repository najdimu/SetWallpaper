package com.example.setwallpaper.personalization

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.setwallpaper.R

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
        holder.author.text = perStyleItem.userName
        holder.image.load(perStyleItem.mainImage){
            error(R.drawable.error_place_holder)
            placeholder(R.drawable.place_holder)
        }
        holder.avatar.load(perStyleItem.avatar) {
            error(R.drawable.error_place_holder)
            placeholder(R.drawable.place_holder)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PersonalStyleDetailActivity::class.java)
            intent.putExtra("title", perStyleItem.title)
            intent.putExtra("author", perStyleItem.userName)
            intent.putExtra("description", perStyleItem.description)
            intent.putExtra("images", perStyleItem.images)
            intent.putExtra("avatar", perStyleItem.avatar)
            holder.itemView.context.startActivity(intent)
        }

    }
}