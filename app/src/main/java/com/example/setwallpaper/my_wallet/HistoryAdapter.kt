package com.example.setwallpaper.my_wallet

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.setwallpaper.R
class HistoryAdapter(var historyList: MutableList<History>, var dat: Int): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {


    var firstNumberWeek = 0
    var firstNameMonth = ""

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layoutDate: ConstraintLayout = view.findViewById(R.id.layout_date)
        val layoutDaily: ConstraintLayout = view.findViewById(R.id.layout_history_daily)
        val layoutWeekly: ConstraintLayout = view.findViewById(R.id.layout_history_weekly)
        val layoutMonthly: ConstraintLayout = view.findViewById(R.id.layout_history_monthly)
        val separateMonthly: View = view.findViewById(R.id.monthly_separate)
        val separateWeekly: View = view.findViewById(R.id.weekly_separate)
        val separateDaily: View = view.findViewById(R.id.daily_separate)

        val monthName: TextView = view.findViewById(R.id.tv_month_name)
        val incomeMonthly: TextView = view.findViewById(R.id.tv_income_amount_monthly)
        val expenseMonthly: TextView = view.findViewById(R.id.tv_expense_amount_monthly)

        val weekNumber: TextView = view.findViewById(R.id.tv_week_number)
        val weekStart: TextView = view.findViewById(R.id.tv_weekly_start)
        val weekEnd: TextView = view.findViewById(R.id.tv_weekly_end)
        val incomeWeekly: TextView = view.findViewById(R.id.tv_income_amount)
        val expenseWeekly: TextView = view.findViewById(R.id.tv_expense_amount)


        val date: TextView = view.findViewById(R.id.tv_date)
        val totalBalance: TextView = view.findViewById(R.id.tv_total_balance)
        val layoutHistory: ConstraintLayout = view.findViewById(R.id.layout_history_daily)
        val iconItem: ImageView = view.findViewById(R.id.image_item_history)
        val typeTitle: TextView = view.findViewById(R.id.tv_type_title)
        val typeDesc: TextView = view.findViewById(R.id.tv_type_description)
        val amount: TextView = view.findViewById(R.id.tv_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]
        val date = history.date.substring(0, 8)
        val daily = historyList.groupBy { it.date.substring(0, 8) }
            .mapValues { (_, trans) ->
                trans.sumOf { it.amount }
            }
        val weekly = historyList.groupBy { it.weekNum }
            .mapValues { (_, monthlyTransactions) ->

                // Partition splits the list into two lists based on the predicate (isExpense)
                val (incomes, expenses) = monthlyTransactions.partition { it.income }

                // Sum the amounts in each partitioned list
                val totalExpense = expenses.sumOf { it.amount }
                val totalIncome = incomes.sumOf { it.amount }

                // Return the results as a Pair (Expense, Income)
                Pair(totalExpense, totalIncome)
            }
        val monthly = historyList.groupBy { it.monthName }
            .mapValues { (_, monthlyTransactions) ->

                // Partition splits the list into two lists based on the predicate (isExpense)
                val (incomes, expenses) = monthlyTransactions.partition { it.income }

                // Sum the amounts in each partitioned list
                val totalExpense = expenses.sumOf { it.amount }
                val totalIncome = incomes.sumOf { it.amount }

                // Return the results as a Pair (Expense, Income)
                Pair(totalExpense, totalIncome)
            }


        when (dat) {

            0 -> {
                holder.layoutMonthly.visibility = View.GONE
                holder.separateMonthly.visibility = View.GONE
                holder.layoutWeekly.visibility = View.GONE
                holder.separateWeekly.visibility = View.GONE
                if (position == historyList.lastIndex) {
                    holder.separateDaily.visibility = View.GONE
                }

                if (historyList.isNotEmpty() && history == historyList[0]) {
                    if (daily.containsKey(date)) {
                        holder.totalBalance.text = daily[date].toString()
                    }
                    holder.date.text = date
                } else if (historyList[position - 1].idDaily == 0) {
                    if (daily.containsKey(date)) {
                        holder.totalBalance.text = daily[date].toString()
                    }
                    holder.date.text = date
                } else {
                    holder.layoutDate.visibility = View.GONE
                }

                holder.typeTitle.text = history.type
                holder.iconItem.load(history.icon)
                if (history.description.isEmpty()) {
                    holder.typeDesc.visibility = View.GONE
                } else {
                    holder.typeDesc.text = history.description
                }
                holder.amount.text = if (history.income) {
                    "+${history.amount} ${history.currency}"
                } else {
                    "${history.amount} ${history.currency}"
                }
                if (!history.income) {
                    holder.amount.setTextColor(getColor(holder.itemView.context, R.color.red))
                }

            }

            1 -> {
                holder.layoutMonthly.visibility = View.GONE
                holder.separateMonthly.visibility = View.GONE
                holder.layoutDate.visibility = View.GONE
                holder.layoutDaily.visibility = View.GONE
                holder.separateDaily.visibility = View.GONE
                if (position == historyList.lastIndex) {
                    holder.separateWeekly.visibility = View.GONE
                }

                if (firstNumberWeek != history.weekNum){
                    firstNumberWeek = history.weekNum

                    holder.weekNumber.text = "${history.weekNum}W"
                    holder.weekStart.text = history.startWeek
                    holder.weekEnd.text = history.endWeek
                    if (weekly[history.weekNum]?.second.toString().isEmpty()){
                        0.0
                    } else {
                        holder.incomeWeekly.text = weekly[history.weekNum]?.second.toString()
                    }
                    if (weekly[history.weekNum]?.first.toString().isEmpty()){
                        0.0
                    } else {
                        holder.expenseWeekly.text = weekly[history.weekNum]?.first.toString()
                    }

                } else{
                    holder.layoutWeekly.visibility = View.GONE
                    holder.separateWeekly.visibility = View.GONE
                }



            }

            2 -> {
                holder.layoutWeekly.visibility = View.GONE
                holder.separateWeekly.visibility = View.GONE
                holder.layoutDate.visibility = View.GONE
                holder.layoutDaily.visibility = View.GONE
                holder.separateDaily.visibility = View.GONE
                if (position == historyList.lastIndex) {
                    holder.separateMonthly.visibility = View.GONE
                }

                if (firstNameMonth != history.monthName){
                    firstNameMonth = history.monthName

                    holder.monthName.text = "${history.monthName}, ${history.date.substring(3,8)}"
                    if (monthly[history.monthName]?.second.toString().isEmpty()){
                        0.0
                    } else {
                        holder.incomeMonthly.text = monthly[history.monthName]?.second.toString()
                    }
                    if (monthly[history.monthName]?.first.toString().isEmpty()){
                        0.0
                    } else {
                        holder.expenseMonthly.text = monthly[history.monthName]?.first.toString()
                    }

                } else{
                    holder.layoutMonthly.visibility = View.GONE
                    holder.separateMonthly.visibility = View.GONE
                }

            }





        }

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
            } else {
                typeText.text = "Expense"
                amountText.text = "${history.amount * -1} ${history.currency}"
                paymentText.visibility = View.VISIBLE
                paymentTextView.visibility = View.VISIBLE
                paymentText.text = history.paymentMethod
            }
            categoryText.text = history.type
            noteText.text = history.description
            dateText.text = history.date.substring(0, 8)
            timeText.text = history.date.substringAfter(" ")

            val dio = Dialog(holder.itemView.context)
            dio.setContentView(dialogView)
            dio.setCanceledOnTouchOutside(false)
            dio.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dio.show()

            buttonOk.setOnClickListener {
                dio.cancel()
            }
        }

    }

        override fun getItemCount() = historyList.size

}