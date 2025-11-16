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
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NewHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewHistoryBinding

    private val viewModel: WalletViewModel by viewModels()
    var categoryImage1 = 0
    var categoryTitle1 = ""
    val walletsName= "wallets_history_list"
    val historyList: MutableList<History> = ArrayList()
    var oldList: MutableList<Wallet> = ArrayList()
    var daily = 1

    val iconDrawableMap = mapOf(
        "am" to R.drawable.baseline_send_24,
        "ao" to R.drawable.baseline_add_24,)

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
        title = textTitle
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        val colorFilter = PorterDuffColorFilter(ContextCompat.getColor(this, R.color.white),
            PorterDuff.Mode.SRC_ATOP)
        toolbar.navigationIcon?.colorFilter = colorFilter

        val currencyIcon = intent.getStringExtra("currency_icon")
        val currencyName = intent.getStringExtra("currency_name")

        binding.currencyIcon.load(currencyIcon)
        binding.currencyNameText.text = currencyName
        binding.btnNext.text = "Next $textTitle"

        val itemsNameExpense = if (textTitle == "Expense") {
            listOf(
                Category(true,R.drawable.baseline_send_24, "Food & Drink"),
                Category(false,R.drawable.baseline_send_24, "Transport"),
                Category(false,R.drawable.baseline_send_24, "Shopping"),
                Category(false,R.drawable.baseline_send_24, "Cafe"),
                Category(false,R.drawable.baseline_send_24, "Housing"),
                Category(false,R.drawable.baseline_send_24, "Clothes"),
                Category(false,R.drawable.baseline_send_24, "Entertainment"),
                Category(false,R.drawable.baseline_send_24, "Education"),
                Category(false,R.drawable.baseline_send_24, "Healthcare"),
                Category(false,R.drawable.baseline_send_24, "Family"),
                Category(false,R.drawable.baseline_send_24, "Debt Payments"),
                Category(false,R.drawable.baseline_send_24, "Others")
            )
        } else {
            listOf(
                Category(true,R.drawable.baseline_send_24, "Salary"),
                Category(false,R.drawable.baseline_send_24, "Allowance"),
                Category(false,R.drawable.baseline_send_24, "Investments"),
                Category(false,R.drawable.baseline_send_24, "Prize"),
                Category(false,R.drawable.baseline_send_24, "Others")
            )
        }
        binding.imageCategory.load(itemsNameExpense[0].image)
        binding.spinnerTypeCategory.text = itemsNameExpense[0].title
        val adapter = CategoryAdapter(itemsNameExpense)
        val view = setUpView()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_category)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        val dialogCategory = setUpCategoryDialog(view)

        adapter.itemClick { category ->
            dialogCategory.cancel()
            val changedList : MutableList<Category> = ArrayList()

            itemsNameExpense.forEach { it->
                if (category == it) {
                    category.selection = true
                } else {
                    it.selection = false
                }
                changedList.add(it)
            }
            adapter.list = changedList
            adapter.notifyDataSetChanged()
            binding.imageCategory.load(category.image)
            binding.spinnerTypeCategory.text = category.title

        }

        binding.noteText.setOnClickListener {
            val now = LocalDateTime.now()
            val now1 = getIsoWeek(now)
        }

        binding.spinnerTypeCategory.setOnClickListener {
            dialogCategory.show()

        }

        binding.btnSave.setOnClickListener {

            var date = ""
            val icon = binding.imageCategory.id
            println(icon)
            val type = binding.spinnerTypeCategory.text.toString()
            val note = binding.editTextNote.text.toString()
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
                val formatter1 = DateTimeFormatter.ofPattern("dd/MM/yy")
                val now = LocalDateTime.now()
                val now1 = getIsoWeek(now)
                date = now.format(formatter)
                System.currentTimeMillis()
                val today = now.format(formatter1)
                val lastDate = sharedPreferences.getString("last_date", "")
                if (today != lastDate){
                    editor.putString("last_date",today).apply()
                    daily = 0
                }

            } else{
                val millis: Long = System.currentTimeMillis()
                val dateObject: Date = Date(millis)

                // Use SimpleDateFormat, being careful about thread safety!
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val formattedDate: String = formatter.format(dateObject)

                // Get the current time in the device's default time zone
                val calendar: Calendar = Calendar.getInstance()

                // Get the week of the year
                // Note: The definition of week-of-year depends on the Calendar's locale/settings.
                val weekOfYear: Int = calendar.get(Calendar.WEEK_OF_YEAR)
                println(formattedDate)
                println(weekOfYear)

            }

            historyList.add(History(daily, date, type,note, amount,currency!!,income))

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

        binding.btnNext.setOnClickListener {

            val icon = binding.imageCategory.id
            binding.currencyIcon.load(icon)

            var date = ""
            val type = itemsNameExpense[categoryImage1].title
            val note = binding.editTextNote.text.toString()
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
                val formatter1 = DateTimeFormatter.ofPattern("dd/MM/yy")
                val now = LocalDateTime.now()
                date = now.format(formatter)

                val today = now.format(formatter1)
                val lastDate = sharedPreferences.getString("last_date", "")
                if (today != lastDate){
                    editor.putString("last_date",today).apply()
                    daily = 0

                }

            } else{

            }

            historyList.add(History(daily, date, type,note, amount,currency!!,income))

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
          //  binding.spinnerTypeCategory.setSelection(0)
        }

    }

    fun setUpView() : View {
        val view = LayoutInflater.from(this)
            .inflate(R.layout.category_dialog, null, false)
        return view
    }
    fun setUpCategoryDialog(view: View) : Dialog {
        val buttonOk = view.findViewById<ImageView>(R.id.image_cancel)

        val dio = Dialog(this)
        dio.setContentView(view)
        dio.setCanceledOnTouchOutside(false)
        dio.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        buttonOk.setOnClickListener {
            dio.cancel()
        }
    return dio
    }

    fun getIsoWeek(dateTime: LocalDateTime): Int {
        // 1. Get the date part (since week number only depends on the date)
        val date = dateTime.toLocalDate()

        // 2. Define the ISO 8601 week rules
        val weekFields = WeekFields.ISO

        // 3. Get the week-of-week-based-year field
        val weekOfYear = date.get(weekFields.weekOfWeekBasedYear())

        return weekOfYear
    }

}