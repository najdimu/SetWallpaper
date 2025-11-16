package com.example.setwallpaper.my_wallet

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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

        if (historyList.isNotEmpty() && history == historyList[0]){
            if (daily.containsKey(date)) { holder.totalBalance.text = daily[date].toString() }
            holder.date.text = date
        }
        else if (historyList[position-1].idDaily == 0){
            if (daily.containsKey(date)) { holder.totalBalance.text = daily[date].toString() }
            holder.date.text = date
        }
        else { holder.layoutDate.visibility = View.GONE }

        holder.typeTitle.text = history.type
        holder.typeDesc.text = history.description
        holder.amount.text = if (history.income) { "+${history.amount} ${history.currency}" }
        else { "${history.amount} ${history.currency}" }
        if (!history.income) {holder.amount.setTextColor(getColor(holder.itemView.context,R.color.red))}


        holder.layoutHistory.setOnClickListener {
            val dialogView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.dialog_item_click, null, false)
            val typeText = dialogView.findViewById<TextView>(R.id.type_text)
            val categoryText = dialogView.findViewById<TextView>(R.id.category_text_dialog)
            val noteText = dialogView.findViewById<TextView>(R.id.note_text_dialog)
            val amountText = dialogView.findViewById<TextView>(R.id.amount_text)
            val paymentText = dialogView.findViewById<TextView>(R.id.payment_text)
            val paymentTextView = dialogView.findViewById<TextView>(R.id.payment_text_view)
            val dateText = dialogView.findViewById<TextView>(R.id.date_text)
            val timeText = dialogView.findViewById<TextView>(R.id.time_text)
            val buttonOk = dialogView.findViewById<Button>(R.id.btn_ok)


            if (history.income) {
                typeText.text = "Income"
                amountText.text = "${history.amount} ${history.currency}"
            }
            else {
                typeText.text = "Expense"
                amountText.text = "${history.amount * -1} ${history.currency}"
                paymentText.visibility = View.VISIBLE
                paymentTextView.visibility = View.VISIBLE
                paymentText.text = "credit"
            }
            categoryText.text = history.type
            noteText.text = history.description
            dateText.text = history.date.substring(0,8)
            timeText.text = history.date.substringAfter(" ")

            val dio = Dialog(holder.itemView.context)
            dio.setContentView(dialogView)
            dio.setCanceledOnTouchOutside(false)
            dio.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dio.show()

            buttonOk.setOnClickListener {
                dio.cancel()
            }
        }

    }

    override fun getItemCount() = historyList.size

}