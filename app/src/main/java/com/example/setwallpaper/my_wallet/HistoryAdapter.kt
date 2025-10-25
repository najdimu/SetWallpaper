package com.example.setwallpaper.my_wallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.setwallpaper.R

class HistoryAdapter(var historyList: MutableList<History>): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val layoutDate: ConstraintLayout = view.findViewById(R.id.layout_date)
        val date: TextView = view.findViewById(R.id.tv_date)
        val totalBalance: TextView = view.findViewById(R.id.tv_total_balance)
        val layoutHistory: ConstraintLayout = view.findViewById(R.id.layout_history_item)
        val typeTitle: TextView = view.findViewById(R.id.tv_type_title)
        val typeDesc: TextView = view.findViewById(R.id.tv_type_description)
        val amount: TextView = view.findViewById(R.id.tv_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent,false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]


        when (history.idDaily) {
            0->{
                holder.date.text = history.date
                holder.typeTitle.text = history.type
                holder.typeDesc.text = history.description
                holder.amount.text = if (history.income){ "+${history.amount}${history.currency}" }
                else { "-${history.amount}${history.currency}" }
            }
            1->{
                holder.layoutDate.visibility = View.GONE
                holder.typeTitle.text = history.type
                holder.typeDesc.text = history.description
                holder.amount.text = if (history.income){ "+${history.amount}${history.currency}" }
                else { "-${history.amount}${history.currency}" }
            }
        }

//
//
//
//
//
//
//
//        var firstDate = historyList[0].date
//        var secondDate = historyList[0].date
//
//        for (item in historyList) {
//            var totalBalance = 0.0
//            if (item.date == firstDate) {
//                holder.layoutDate.visibility = View.VISIBLE
//                holder.date.text = firstDate
//                totalBalance += item.amount
//                secondDate = firstDate
//            }
//            else if (item.date == secondDate){
//
//            }
//
//            else {
//                firstDate = item.date
//            }
//        }
//
//
//



    }

    override fun getItemCount() = historyList.size

    fun updateList(new: MutableList<History>){
        historyList = new
        notifyDataSetChanged()
    }

}