package com.example.setwallpaper.my_wallet

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.ActivityNewHistoryBinding
import org.json.JSONArray
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.core.content.edit
import com.google.gson.Gson

class NewHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewHistoryBinding

    private val viewModel: WalletViewModel by viewModels()
    var spinnerPosition = 0
    var nameCategor = ""

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

        var historyList: MutableList<History> = ArrayList()

        val historyString = sharedPreferences.getString("all_histories_list", "")
        val faqList = Gson().fromJson(historyString, Array<History>::class.java).toList()
        historyList.addAll(faqList)

        historyString?.let {
            if (it.isEmpty()) {
                historyList = ArrayList()
            } else{
                val histories = JSONArray()
                for (i in 0 until histories.length()) {
                    val json = histories.getJSONObject(i)
                    val his = History(json.getInt("idDaily"),
                        json.getString("date"),
                        json.getString("type"),
                        json.getString("description"),
                        json.getDouble("amount"),
                        json.getString("currency"),
                        json.getBoolean("income"))

                    historyList.add(his)
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

        val itemsNameExpense = if (textTitle == "Expense") {
            listOf("Food & Drink", "Transport", "Shopping", "Cafe", "Housing", "Clothes",
                "Entertainment", "Education", "Healthcare", "Family", "Debt Payments", "Others")
        } else {
            listOf("Salary", "Allowance", "Investments", "Prize", "Others")
        }

        val spinnerAdapter = object: ArrayAdapter<String>(this, R.layout.spinner_layout, itemsNameExpense) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView

                if (position == spinnerPosition){
                    textView.setTextColor(ContextCompat.getColor(this@NewHistoryActivity, R.color.green))
                } else {
                    textView.setTextColor(resources.getColor(R.color.black, null))
                }
                return view
            }
        }
        binding.spinnerTypeCategory.adapter = spinnerAdapter
        binding.spinnerTypeCategory.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (p2){
                    0->{
                        spinnerPosition = 0
                        nameCategor = itemsNameExpense[0]
                    }
                    1->{
                        spinnerPosition = 1
                        nameCategor = itemsNameExpense[1]
                    }
                    2->{
                        spinnerPosition = 2
                        nameCategor = itemsNameExpense[2]
                    }
                    3->{
                        spinnerPosition = 3
                        nameCategor = itemsNameExpense[3]
                    }
                    4->{
                        spinnerPosition = 4
                        nameCategor = itemsNameExpense[4]
                    }
                    5->{
                        spinnerPosition = 5
                        nameCategor = itemsNameExpense[5]
                    }
                    6->{
                        spinnerPosition = 6
                        nameCategor = itemsNameExpense[6]
                    }
                    7->{
                        spinnerPosition = 7
                        nameCategor = itemsNameExpense[7]
                    }
                    8->{
                        spinnerPosition = 8
                        nameCategor = itemsNameExpense[8]
                    }
                    9->{
                        spinnerPosition = 9
                        nameCategor = itemsNameExpense[9]
                    }
                    10->{
                        spinnerPosition = 10
                        nameCategor = itemsNameExpense[10]
                    }
                    11->{
                        spinnerPosition = 11
                        nameCategor = itemsNameExpense[11]
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.btnSave.setOnClickListener {
            var daily = 1
            var date = ""
            val type = itemsNameExpense[spinnerPosition]
            val note = binding.editTextNote.text.toString()
            val amount = binding.editTextAmount.text.toString().toDouble()
            val currency = currencyName?.substringBefore(" ")
            val income = textTitle != "Expense"
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
                    Toast.makeText(this, "daily", Toast.LENGTH_SHORT).show()
                }

            } else{

            }

            val new = History(daily, date, type,note, amount,currency!!,income)
            historyList.add(new)

            val gson = Gson()
            val nee = gson.toJson(historyList)
            editor.putString("all_histories_list", nee).apply()

            println(nee)
            finish()

        }

    }
}