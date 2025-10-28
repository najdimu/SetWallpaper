package com.example.setwallpaper.my_wallet

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.ActivityMyWalletBinding
import com.example.setwallpaper.style_zone.activity.AddPersonalStyleActivity
import com.example.setwallpaper.style_zone.style.PersonalStyleItem
import com.google.gson.Gson
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.toString

class MyWalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyWalletBinding
    val currencyIcon= "https://img.samsungapps.com/productNew/000006244780/ENG/ScreenShot_202204220304211621_450_800_1.png"
    val currencyName= "USD (United states dollar)"
    val walletsName= "wallets_history_list"
    var historyList: MutableList<History> = ArrayList()
    var walletList: MutableList<Wallet> = ArrayList()
    val CURREN = "7119643494_" + currencyName?.substringBefore(" ")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("my_wallet", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val walletHistory = sharedPreferences.getString(walletsName, "")
        if (walletHistory.isNullOrEmpty()){
            println()
            val wallet = mutableListOf(Wallet(CURREN, 0.0, emptyList()))
            val gson = Gson()
            val json = gson.toJson(wallet)
            editor.putString(walletsName,json).apply()
        }
        else {
            walletList = Gson().fromJson(walletHistory, Array<Wallet>::class.java).toMutableList()
            walletList.forEach {wallet ->
                if (wallet.id == CURREN) {
                    binding.balanceNumberView.text = wallet.balance.toString()
                    historyList.addAll(wallet.history)
                }
            }
        }



        val toolbar = binding.myWalletToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) ?: true
        toolbar.setNavigationOnClickListener { finish() }
        title = "My Wallet"
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        val colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(this, R.color.white1),
            PorterDuff.Mode.SRC_ATOP
        )
        toolbar.navigationIcon?.colorFilter = colorFilter

        val jsonList = listOf(
            History(0,"2025-10-17 17:15", "Restaurant", "soup with tomato juice", 250.0, "TMT",false),
            History(1,"2025-10-17 18:15", "Restaurant", "soup with tomato juice", 150.0, "TMT",false),
            History(0,"2025-10-18 17:15", "Restaurant", "soup with tomato juice", 250.0, "TMT",false),
            History(1,"2025-10-18 19:15", "Salary", "monthly income", 150.0, "TMT",true),
            History(1,"2025-10-18 21:15", "Restaurant", "soup with tomato juice", 250.0, "TMT",false),
            History(0,"2025-11-19 15:15", "Restaurant", "soup with tomato juice", 250.0, "TMT",false),
            History(0,"2025-12-19 17:15", "Restaurant", "soup with tomato juice", 250.0, "TMT",false)
        )

        binding.recHistory.layoutManager = LinearLayoutManager(this)
        val reser = historyList.reversed().toMutableList()
        binding.recHistory.adapter = HistoryAdapter(reser)

        binding.btnExpense.setOnClickListener {
            val intent = Intent(this, NewHistoryActivity::class.java)
            intent.putExtra("title_text", "Expense")
            intent.putExtra("wallet_id", CURREN)
            intent.putExtra("currency_icon", currencyIcon)
            intent.putExtra("currency_name", currencyName)
            startActivity(intent)
        }

        binding.btnIncome.setOnClickListener {
            val intent = Intent(this, NewHistoryActivity::class.java)
            intent.putExtra("title_text", "Income")
            intent.putExtra("wallet_id", CURREN)
            intent.putExtra("currency_icon", currencyIcon)
            intent.putExtra("currency_name", currencyName)
            startActivity(intent)
        }

        binding.editBalance.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_edit_text, null)
            val inputField = dialogView.findViewById<EditText>(R.id.edit_text_dialog)
            val oldBalance = binding.balanceNumberView.text.toString()
            inputField.setText(oldBalance)


            val builder = AlertDialog.Builder(this)
                .setTitle("Edit Balance")
                .setView(dialogView)
                .setPositiveButton("Save") { dialog, which ->
                    val newText = inputField.text.toString()
                    if (newText.isEmpty()){
                        binding.balanceNumberView.text = "0.0"
                        walletList.forEach { wallet ->
                            if (wallet.id == CURREN){
                                wallet.balance = 0.0
                            }
                        }
                    } else {
                        binding.balanceNumberView.text = newText
                        walletList.forEach { wallet ->
                            if (wallet.id == CURREN){
                                wallet.balance = newText.toDouble()
                            }
                        }
                    }
                    val gson = Gson()
                    val json = gson.toJson(walletList)
                    editor.putString(walletsName,json).apply()
                    println(json)
                    Toast.makeText(this, "Saved: $newText", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.cancel()
                }
            builder.show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        }
        else{

        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("my_wallet", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val walletHistory = sharedPreferences.getString(walletsName, "")
        if (walletHistory.isNullOrEmpty()){
            val wallet = mutableListOf(Wallet(CURREN, 0.0, emptyList()))
            val gson = Gson()
            val json = gson.toJson(wallet)
            editor.putString(walletsName,json).apply()
        }
        else {
            walletList = Gson().fromJson(walletHistory, Array<Wallet>::class.java).toMutableList()
            walletList.forEach {wallet ->
                if (wallet.id == CURREN) {
                    binding.balanceNumberView.text = wallet.balance.toString()
                    historyList = wallet.history.toMutableList()
                }
            }
        }
        val reser = historyList.reversed().toMutableList()
        binding.recHistory.adapter = HistoryAdapter(reser)
    }
}
/*
 if (it.isEmpty()) {
                historyList = ArrayList()
            }
            else{
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
                    println(his)
                }
            }
 */