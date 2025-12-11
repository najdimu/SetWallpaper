package com.example.setwallpaper.my_wallet

import android.app.Dialog
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.ActivityNewHistoryBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NewHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewHistoryBinding
    var categoryPosition = 0
    var paymentPosition = 0
    val walletsName= "wallets_history_list"
    val historyList: MutableList<History> = ArrayList()
    var oldList: MutableList<Wallet> = ArrayList()
    var daily = 1
    var weekNumber = 0
    var startWeek = ""
    var endWeek = ""
    var monthName = ""
    var date = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("my_wallet", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val walletId = intent.getStringExtra("wallet_id")

        val walletHistory = sharedPreferences.getString(walletsName, "")
        if (!walletHistory.isNullOrEmpty() && !walletId.isNullOrEmpty()){
            oldList = Gson().fromJson(walletHistory, Array<Wallet>::class.java).toMutableList()
            println(oldList)
            oldList.forEach { wallet ->
                if (wallet.id == walletId) {
                    historyList.addAll(wallet.history)
                }
            }
        }

        val textTitle = intent.getStringExtra("title_text") ?: "Expense"

        val toolbar = binding.newHistoryToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) ?: true
        toolbar.setNavigationOnClickListener { finish() }
        if (textTitle == "Expense"){
            title = getString(R.string.expense_text)
            binding.btnNext.text = getString(R.string.next_expense)
        } else{
            binding.btnNext.text = getString(R.string.next_income)
            title = getString(R.string.income_text)
            binding.paymentMethodText.visibility = View.GONE
            binding.paymentMethodType.visibility = View.GONE
            binding.iconPaymentMethod.visibility = View.GONE
        }
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        val colorFilter = PorterDuffColorFilter(ContextCompat.getColor(this, R.color.white),
            PorterDuff.Mode.SRC_ATOP)
        toolbar.navigationIcon?.colorFilter = colorFilter

        val currencyIcon = intent.getStringExtra("currency_icon")
        val currencyName = intent.getStringExtra("currency_name")

        binding.currencyIcon.load(currencyIcon)
        binding.currencyNameText.text = currencyName

        val itemsCategory = if (textTitle == "Expense") {
            listOf(
                Category(true,R.drawable.food_drink, getString(R.string.food_drink)),
                Category(false,R.drawable.transport, getString(R.string.transport)),
                Category(false,R.drawable.shopping, getString(R.string.shopping)),
                Category(false,R.drawable.cafe, getString(R.string.cafe)),
                Category(false,R.drawable.housing, getString(R.string.housing)),
                Category(false,R.drawable.clothes, getString(R.string.clothes)),
                Category(false,R.drawable.entertainment, getString(R.string.entertainment)),
                Category(false,R.drawable.education, getString(R.string.education)),
                Category(false,R.drawable.healthcare, getString(R.string.healthcare)),
                Category(false,R.drawable.family, getString(R.string.family)),
                Category(false,R.drawable.debt_payments, getString(R.string.debt_payments)),
                Category(false,R.drawable.others, getString(R.string.others))
            )
        } else {
            listOf(
                Category(true,R.drawable.salary, getString(R.string.salary)),
                Category(false,R.drawable.allowance, getString(R.string.allowance)),
                Category(false,R.drawable.investment, getString(R.string.investments)),
                Category(false,R.drawable.prize, getString(R.string.prize)),
                Category(false,R.drawable.others, getString(R.string.others))
            )
        }
        categoryPosition = itemsCategory[0].icon
        binding.imageCategory.load(itemsCategory[0].icon)
        binding.typeCategory.text = itemsCategory[0].title
        val categoryAdapter = CategoryAdapter(itemsCategory)
        val viewCategory = setUpView()
        val recyclerCategory = viewCategory.findViewById<RecyclerView>(R.id.recycler_category)
        recyclerCategory.layoutManager = LinearLayoutManager(this)
        recyclerCategory.adapter = categoryAdapter
        val dialogCategory = setUpCategoryDialog(viewCategory,getString(R.string.category_text))

        binding.typeCategory.setOnClickListener {
            dialogCategory.show()
        }

        categoryAdapter.itemClick { category ->
            dialogCategory.cancel()
            val changedList : MutableList<Category> = ArrayList()

            itemsCategory.forEach { it->
                if (category == it) {
                    category.selection = true
                } else {
                    it.selection = false
                }
                changedList.add(it)
            }
            categoryAdapter.list = changedList
            categoryAdapter.notifyDataSetChanged()
            categoryPosition = category.icon

            binding.imageCategory.load(category.icon)
            binding.typeCategory.text = category.title

        }

        val paymentList = listOf(
            Category(true,R.drawable.cash, getString(R.string.cash)),
            Category(false,R.drawable.credit_card, getString(R.string.credit_card)),
            Category(false,R.drawable.online_wallet, getString(R.string.online_wallet)),
            Category(false,R.drawable.others, getString(R.string.others))
        )
        paymentPosition = paymentList[0].icon
        binding.iconPaymentMethod.load(paymentList[0].icon)
        binding.paymentMethodType.text = paymentList[0].title

        val paymentAdapter = CategoryAdapter(paymentList)
        val viewPayment = setUpView()
        val recyclerPayment = viewPayment.findViewById<RecyclerView>(R.id.recycler_category)
        recyclerPayment.layoutManager = LinearLayoutManager(this)
        recyclerPayment.adapter = paymentAdapter
        val dialogPayment = setUpCategoryDialog(viewPayment,getString(R.string.payment_method_text))

        binding.paymentMethodType.setOnClickListener {
            dialogPayment.show()
        }

        paymentAdapter.itemClick { payment ->
            dialogPayment.cancel()
            val changedList : MutableList<Category> = ArrayList()

            paymentList.forEach { it->
                if (payment == it) {
                    payment.selection = true
                } else {
                    it.selection = false
                }
                changedList.add(it)
            }
            paymentAdapter.list = changedList
            paymentAdapter.notifyDataSetChanged()
            paymentPosition = payment.icon

            binding.iconPaymentMethod.load(payment.icon)
            binding.paymentMethodType.text = payment.title

        }

        binding.btnSave.setOnClickListener {

            val type = binding.typeCategory.text.toString()
            val note = binding.editTextNote.text.toString()
            val paymentMethod = binding.paymentMethodType.text.toString()
            val income = textTitle != "Expense"
            val amount = when {
                binding.editTextAmount.text.toString().isEmpty() -> 0.0
                else ->  if(income){
                    binding.editTextAmount.text.toString().toDouble()
                } else {
                    -binding.editTextAmount.text.toString().toDouble()
                }
            }
            val currency = currencyName?.substringBefore(" ")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")
                val formatterWeek = DateTimeFormatter.ofPattern("dd/MM/yy")
                val formatterMonth = DateTimeFormatter.ofPattern("MMMM")
                val now = LocalDateTime.now()
                val now1 = LocalDate.now()

                date = now.format(formatter)
                monthName = now.format(formatterMonth)
                val today = date.substring(0,8)
                val lastDate = sharedPreferences.getString("last_date", "")
                if (today != lastDate){
                    editor.putString("last_date",today).apply()
                    daily = 0
                }
                // 2. Define the ISO 8601 week rules
                val weekFields = WeekFields.ISO
                // 3. Get the week-of-week-based-year field
                weekNumber = now.get(weekFields.weekOfWeekBasedYear())

                val startWeekDate: LocalDate = now1.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val endWeekDate: LocalDate = startWeekDate.plusDays(6)

                startWeek = startWeekDate.format(formatterWeek)
                endWeek = endWeekDate.format(formatterWeek)

            }
            else{
                val millis: Long = System.currentTimeMillis()
                val dateObject: Date = Date(millis)

                // Use SimpleDateFormat, being careful about thread safety!
                val formatter = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
                date = formatter.format(dateObject)

                val today = date.substring(0,8)
                val lastDate = sharedPreferences.getString("last_date", "")
                if (today != lastDate){
                    editor.putString("last_date",today).apply()
                    daily = 0
                }

                // Get the current time in the device's default time zone
                val calendar: Calendar = Calendar.getInstance()

                // Get the week of the year
                // Note: The definition of week-of-year depends on the Calendar's locale/settings.
                weekNumber = calendar.get(Calendar.WEEK_OF_YEAR)

                //  val calendar: Calendar = Calendar.getInstance(Locale.getDefault())
                calendar.firstDayOfWeek = Calendar.MONDAY
                calendar.minimalDaysInFirstWeek = 4

                // The desired week number (from Option 1, or set manually)
                val targetYear = calendar.get(Calendar.YEAR)

                // Set the calendar to the target week and year
                calendar.set(Calendar.YEAR, targetYear)
                calendar.set(Calendar.WEEK_OF_YEAR, weekNumber)

                // 1. Get StartWeek (Monday)
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                startWeek = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(calendar.time)

                // 2. Get EndWeek (Sunday)
                calendar.add(Calendar.DAY_OF_YEAR, 6) // Add 6 days to Monday
                endWeek = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(calendar.time)

                // Use "MMMM" for the full month name
                val fullMonthFormatter = SimpleDateFormat("MMMM", Locale.getDefault())
                monthName = fullMonthFormatter.format(dateObject)

            }
            if (amount != 0.0){
                historyList.add(History(daily, weekNumber, startWeek, endWeek, monthName,
                    date, type,categoryPosition, note, paymentPosition, paymentMethod, amount,currency!!,income))

                oldList.forEach { wallet ->
                    if (wallet.id == walletId) {
                        wallet.history = historyList
                        wallet.balance = wallet.balance + amount
                    }
                }
                val json = Gson().toJson(oldList)
                editor.putString(walletsName, json).apply()
                finish()
            }
            else{
                binding.errorTextAmount.visibility = View.VISIBLE
            }
        }

        binding.btnNext.setOnClickListener {

            val type = binding.typeCategory.text.toString()
            val note = binding.editTextNote.text.toString()
            val paymentMethod = binding.paymentMethodType.text.toString()
            val income = textTitle != "Expense"
            val amount = when {
                binding.editTextAmount.text.toString().isEmpty() -> 0.0
                else ->  if(income){
                    binding.editTextAmount.text.toString().toDouble()
                } else {
                    -binding.editTextAmount.text.toString().toDouble()
                }
            }
            val currency = currencyName?.substringBefore(" ")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")
                val formatterWeek = DateTimeFormatter.ofPattern("dd/MM/yy")
                val formatterMonth = DateTimeFormatter.ofPattern("MMMM")
                val now = LocalDateTime.now()
                val now1 = LocalDate.now()

                date = now.format(formatter)
                monthName = now.format(formatterMonth)
                val today = date.substring(0,8)
                val lastDate = sharedPreferences.getString("last_date", "")
                if (today != lastDate){
                    editor.putString("last_date",today).apply()
                    daily = 0
                }
                // 2. Define the ISO 8601 week rules
                val weekFields = WeekFields.ISO
                // 3. Get the week-of-week-based-year field
                weekNumber = now.get(weekFields.weekOfWeekBasedYear())

                val startWeekDate: LocalDate = now1.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val endWeekDate: LocalDate = startWeekDate.plusDays(6)

                startWeek = startWeekDate.format(formatterWeek)
                endWeek = endWeekDate.format(formatterWeek)
            }
            else{
                val millis: Long = System.currentTimeMillis()
                val dateObject: Date = Date(millis)

                // Use SimpleDateFormat, being careful about thread safety!
                val formatter = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
                date = formatter.format(dateObject)

                val today = date.substring(0,8)
                val lastDate = sharedPreferences.getString("last_date", "")
                if (today != lastDate){
                    editor.putString("last_date",today).apply()
                    daily = 0
                }

                // Get the current time in the device's default time zone
                val calendar: Calendar = Calendar.getInstance()

                // Get the week of the year
                // Note: The definition of week-of-year depends on the Calendar's locale/settings.
                weekNumber = calendar.get(Calendar.WEEK_OF_YEAR)

                //  val calendar: Calendar = Calendar.getInstance(Locale.getDefault())
                calendar.firstDayOfWeek = Calendar.MONDAY
                calendar.minimalDaysInFirstWeek = 4

                // The desired week number (from Option 1, or set manually)
                val targetYear = calendar.get(Calendar.YEAR)

                // Set the calendar to the target week and year
                calendar.set(Calendar.YEAR, targetYear)
                calendar.set(Calendar.WEEK_OF_YEAR, weekNumber)

                // 1. Get StartWeek (Monday)
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                startWeek = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(calendar.time)

                // 2. Get EndWeek (Sunday)
                calendar.add(Calendar.DAY_OF_YEAR, 6) // Add 6 days to Monday
                endWeek = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(calendar.time)

                // Use "MMMM" for the full month name
                val fullMonthFormatter = SimpleDateFormat("MMMM", Locale.getDefault())
                monthName = fullMonthFormatter.format(dateObject)

            }

            if (amount != 0.0){
                historyList.add(History(daily, weekNumber, startWeek, endWeek, monthName,
                    date, type,categoryPosition, note, paymentPosition, paymentMethod,amount,currency!!,income))

                oldList.forEach { wallet ->
                    if (wallet.id == walletId) {
                        wallet.history = historyList
                        wallet.balance = wallet.balance + amount
                    }
                }
                val json = Gson().toJson(oldList)
                editor.putString(walletsName, json).apply()
                binding.editTextAmount.text.clear()
                binding.editTextNote.text.clear()
                binding.errorTextAmount.visibility = View.GONE
            }
            else{
                binding.errorTextAmount.visibility = View.VISIBLE
            }
        }
    }

    fun setUpView() : View {
        val view = LayoutInflater.from(this)
            .inflate(R.layout.category_dialog, null, false)
        return view
    }
    fun setUpCategoryDialog(view: View, text: String) : Dialog {
        val buttonOk = view.findViewById<ImageView>(R.id.image_cancel)
        val title = view.findViewById<TextView>(R.id.title_category)
        title.text = text

        val dio = Dialog(this)
        dio.setContentView(view)
        dio.setCanceledOnTouchOutside(false)
        dio.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        buttonOk.setOnClickListener {
            dio.cancel()
        }
    return dio
    }

}