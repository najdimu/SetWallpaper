package com.example.setwallpaper

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class FAQAdapter(private var faqList: MutableList<FAQsItem>) : RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    val expandedItems = mutableSetOf<Int>()

    class FAQViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val question: TextView = view.findViewById(R.id.faq_question_text)
        val answer: TextView = view.findViewById(R.id.faq_answer_text)
        val cardView: CardView = view as CardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.faq_item_layout, parent, false)
        return FAQViewHolder(view)
    }

    override fun getItemCount() = faqList.size

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val faq = faqList[position]
        holder.question.text = faq.title
        holder.answer.text = faq.info

        holder.question.setOnClickListener {
            val isExpanded = holder.answer.visibility == View.GONE
            if (isExpanded){
                animateExpand(holder.answer)
            }
            else{
                animateCollapse(holder.answer)
            }
        }

    }

    fun updateData(newData: List<FAQsItem>) {
        faqList.clear()
        faqList.addAll(newData)
        notifyDataSetChanged()  // Notify RecyclerView
    }


    private fun animateExpand(view: View){
        view.visibility = View.VISIBLE
        ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).setDuration(300).start()
    }
    private fun animateCollapse(view: View){
        ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).setDuration(300).start()
        view.postDelayed({view.visibility = View.GONE}, 300)
    }


}