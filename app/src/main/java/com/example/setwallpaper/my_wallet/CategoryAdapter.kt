package com.example.setwallpaper.my_wallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.setwallpaper.R

class CategoryAdapter(val categories: List<Category>): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    var list = categories

    class CategoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val layout: ConstraintLayout = view.findViewById(R.id.layout_category_item)
        val image: ImageView = view.findViewById(R.id.icon_item_category)
        val title: TextView = view.findViewById(R.id.title_item_category)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = list[position]
        holder.image.load(category.icon)
        holder.title.text = category.title
        if (category.selection){
            holder.layout.setBackgroundColor(ContextCompat.getColor(holder.layout.context,R.color.white2))
        }
        else{
            holder.layout.setBackgroundColor(ContextCompat.getColor(holder.layout.context,R.color.white))

        }

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