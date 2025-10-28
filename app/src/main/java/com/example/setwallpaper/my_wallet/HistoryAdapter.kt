package com.example.setwallpaper.my_wallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
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
        val date = history.date.substring(0,8)
        val daily =  historyList.groupBy { it.date.substring(0,8) }
            .mapValues { (_, trans)->
                trans.sumOf { it.amount }
            }

        when (history.idDaily) {
            0->{
                if (historyList.isNotEmpty() && history == historyList[0]){
                    if (daily.containsKey(date)) {
                        holder.totalBalance.text = daily[date].toString()
                    }
                    holder.date.text = date
                }
                else if (historyList[position-1].idDaily == 0){
                    if (daily.containsKey(date)) {
                        holder.totalBalance.text = daily[date].toString()
                    }
                    holder.date.text = date
                } else {
                    holder.layoutDate.visibility = View.GONE
                }
                holder.typeTitle.text = history.type
                holder.typeDesc.text = history.description
                holder.amount.text = if (history.income) { "+${history.amount} ${history.currency}" }
                else { "${history.amount} ${history.currency}" }
                if (!history.income) {holder.amount.setTextColor(getColor(holder.itemView.context,R.color.red))}
            }
            1->{
                if (historyList.isNotEmpty() && history == historyList[0]){
                    if (daily.containsKey(date)) {
                        holder.totalBalance.text = daily[date].toString()
                    }
                    holder.date.text = date
                }
                else if (historyList[position-1].idDaily == 0){
                    if (daily.containsKey(date)) {
                        holder.totalBalance.text = daily[date].toString()
                    }
                    holder.date.text = date
                } else{
                    holder.layoutDate.visibility = View.GONE
                }
                holder.typeTitle.text = history.type
                holder.typeDesc.text = history.description
                holder.amount.text = if (history.income){ "+${history.amount} ${history.currency}" }
                else { "${history.amount} ${history.currency}" }
                if (!history.income) {holder.amount.setTextColor(getColor(holder.itemView.context,R.color.red))}
            }
        }

    }

    override fun getItemCount() = historyList.size

}