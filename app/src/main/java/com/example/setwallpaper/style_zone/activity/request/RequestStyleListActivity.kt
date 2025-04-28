package com.example.setwallpaper.style_zone.activity.request

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.ActivityRequestStyleListBinding
import com.example.setwallpaper.style_zone.style.PersonalStyleAdapter
import com.example.setwallpaper.style_zone.style.PersonalStyleItem
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class RequestStyleListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestStyleListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRequestStyleListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.request_style_list_activity_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = binding.toolbarRequestStyleList
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) ?: true
        toolbar.setNavigationOnClickListener { finish() }
        title = resources.getString(R.string.toolbar_title_personal_style_detail)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        val colorFilter = PorterDuffColorFilter(ContextCompat.getColor(this, R.color.white),
            PorterDuff.Mode.SRC_ATOP)
        toolbar.navigationIcon?.colorFilter = colorFilter

        binding.requestStyleListRecyclerview.layoutManager = LinearLayoutManager(this)
        val sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val userId = sharedPreferences.getString("userId", "")
        if (userId != null) {
            if (userId.isNotEmpty()){
                getRequestData(userId){  styleList ->
                    println(styleList)
                    println(userId)

                    runOnUiThread {
                        if (styleList.isNullOrEmpty()){
                            binding.textviewRequestIfEmpty.visibility = View.VISIBLE
                        }
                        styleList?.let {
                            val requestList: MutableList<RequestStyleItem> = ArrayList()
                            requestList.addAll(styleList)
                            binding.requestStyleListRecyclerview.adapter = RequestStyleAdapter(requestList)
                            if (requestList.size != 0) {
                                editor.putInt("requestItemNumber", requestList.size)
                                editor.apply()
                            } else {
                                editor.putInt("requestItemNumber", -1)
                                editor.apply()
                            }
                        }
                    }
                }
            }
        }
        Toast.makeText(this, userId, Toast.LENGTH_LONG).show()

    }

    private fun getRequestData(userId: String, onResult: (List<RequestStyleItem>?) -> Unit){
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://raw.githubusercontent.com/najdimu/Stock/refs/heads/main/request_style_data.json")
            .addHeader("User-ID", userId)
            .build()
        
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { json ->
                    println(json)
                    val requestStyle = Gson().fromJson(json, Array<RequestStyleItem>::class.java).toList()
                    onResult(requestStyle)
                } ?: onResult(null)
            }
        })
            
    }

}