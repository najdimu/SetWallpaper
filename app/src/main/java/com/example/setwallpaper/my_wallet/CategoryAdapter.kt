package com.example.setwallpaper.my_wallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.setwallpaper.R

class CategoryAdapter(val categories: List<Category>): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    var list = categories

    class CategoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image_item_category)
        val title: TextView = view.findViewById(R.id.item_title_category)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, null, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = list[position]
        holder.image.load(category.image)
        holder.title.text = category.title
        if (category.selection){
            holder.title.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.white1))
        }
        else{
            holder.title.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.white))

        }
        println(category.title)
        println(category.selection)



        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(category) }
        }
    }

    override fun getItemCount(): Int = list.size

    private var onItemClickListener: ((Category) -> Unit)? = null


    fun itemClick(listener: (Category) -> Unit) {
        onItemClickListener = listener
    }


}