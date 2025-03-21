package com.example.setwallpaper.personalization

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.ActivityPersonalStyleBinding

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



    }
}