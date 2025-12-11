package com.example.setwallpaper.my_wallet

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.ActivityMyWalletBinding
import com.google.gson.Gson

class MyWalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyWalletBinding
    val currencyIcon= "https://img.samsungapps.com/productNew/000006244780/ENG/ScreenShot_202204220304211621_450_800_1.png"
    val currencyName= "USD (United states dollar)"
    val walletsName= "wallets_history_list"
    var historyList: MutableList<History> = ArrayList()
    var walletList: MutableList<Wallet> = ArrayList()
    val CURREN = "7119643494_" + currencyName?.substringBefore(" ")

    var spinnerPos = 0


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

            val wallet = mutableListOf(Wallet(CURREN, 0.0, emptyList()))
            val gson = Gson()
            val json = gson.toJson(wallet)
            editor.putString(walletsName,json).apply()
            println(json)
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
        title = getString(R.string.my_wallet)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        val colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(this, R.color.white1),
            PorterDuff.Mode.SRC_ATOP
        )
        toolbar.navigationIcon?.colorFilter = colorFilter

        binding.recHistory.layoutManager = LinearLayoutManager(this)
        binding.recHistory.adapter = HistoryAdapter(historyList,spinnerPos)



        val itemNames = listOf(getString(R.string.daily), getString(R.string.weekly), getString(R.string.monthly))
        
        val spinnerAdapter = object: ArrayAdapter<String>(this, R.layout.spinner_layout_2, itemNames) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView

                if (position == spinnerPos){
                    textView.setTextColor(ContextCompat.getColor(this@MyWalletActivity, R.color.green))
                } else {
                    textView.setTextColor(resources.getColor(R.color.black, null))
                }
                return view
            }
        }
        binding.historySpinner.adapter = spinnerAdapter
        binding.historySpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (p2){
                    0->{
                        spinnerPos = 0
                        binding.recHistory.adapter = HistoryAdapter(historyList, spinnerPos)
                    }
                    1->{
                        spinnerPos = 1
                        binding.recHistory.adapter = HistoryAdapter(historyList, spinnerPos)
                    }
                    2->{
                        spinnerPos = 2
                        binding.recHistory.adapter = HistoryAdapter(historyList, spinnerPos)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }


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

        binding.balanceTextView.setOnClickListener {
        }

        binding.editBalance.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_edit_text, null)
            val inputField = dialogView.findViewById<EditText>(R.id.edit_text_dialog)
            val oldBalance = binding.balanceNumberView.text.toString()
            inputField.setText(oldBalance)


            val builder = AlertDialog.Builder(this)
                .setTitle(getString(R.string.edit_balance))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.save_text)) { dialog, which ->
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
                }
                .setNegativeButton(getString(R.string.cancel_text)) { dialog, which ->
                    dialog.cancel()
                }
            builder.show()
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
        binding.recHistory.adapter = HistoryAdapter(historyList, spinnerPos)
    }
}