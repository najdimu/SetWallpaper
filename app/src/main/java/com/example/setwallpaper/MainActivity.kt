package com.example.setwallpaper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setwallpaper.databinding.ActivityMainBinding
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FAQAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnFAQPage.setOnClickListener {
            finish()
        }

        binding.FAQRecyclerview.layoutManager = LinearLayoutManager(this)
        adapter = FAQAdapter(mutableListOf())
        binding.FAQRecyclerview.adapter = adapter

        getFAQData() { list ->
            runOnUiThread {
                list?.let {
                    adapter.updateData(it)
                }
            }
        }

        binding.textFAQPageTapHere.setOnClickListener {
            // link to report a problem page
        }

    }

    private fun getFAQData(onResult: (List<FAQsItem>?) -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder().url("https://raw.githubusercontent.com/najdimu/Stock/refs/heads/main/faq_data.json").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { json ->
                    println(json)
                    val faqList = Gson().fromJson(json, Array<FAQsItem>::class.java).toList()
                    onResult(faqList)
                } ?: onResult(null)
            }
        })
    }

}

