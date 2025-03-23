package com.example.setwallpaper.personalization

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.ActivityPersonalStyleBinding
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException

class PersonalStyleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalStyleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPersonalStyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.personal_style_activity_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = binding.toolbarPersonalStyle
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) ?: true
        toolbar.setNavigationOnClickListener {
            finish()
        }
        title = resources.getString(R.string.toolbar_title_personal_style)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        val colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(this, R.color.white1),
            PorterDuff.Mode.SRC_ATOP
        )
        toolbar.navigationIcon?.colorFilter = colorFilter

        binding.btnAddPersonalStyle.setOnClickListener {
            startActivity(Intent(this, AddPersonalStyleActivity::class.java))
        }

        binding.personalStyleRecyclerview.layoutManager = LinearLayoutManager(this)

        getFAQData {
            list ->
            runOnUiThread {
                list?.let {
                    val styleList: MutableList<PersonalStyleItem> = ArrayList()
                    styleList.addAll(list)
                    binding.personalStyleRecyclerview.adapter = PersonalStyleAdapter(styleList)
                }
            }
        }
    }

    private fun getFAQData(onResult: (List<PersonalStyleItem>?) -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder().url("https://raw.githubusercontent.com/najdimu/Stock/refs/heads/main/style_data.json").build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { json ->
                    println(json)
                    val faqList = Gson().fromJson(json, Array<PersonalStyleItem>::class.java).toList()
                    onResult(faqList)
                } ?: onResult(null)
            }
        })
    }

}