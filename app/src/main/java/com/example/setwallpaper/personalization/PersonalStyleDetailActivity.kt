package com.example.setwallpaper.personalization

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.ActivityPersonalStyleDetailBinding
import java.io.Serializable

class PersonalStyleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalStyleDetailBinding

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPersonalStyleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.personal_style_detail_activity_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = binding.toolbarPersonalStyleDetail
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) ?: true
        toolbar.setNavigationOnClickListener {
            finish()
        }

        title = intent.getStringExtra("title") ?: resources.getString(R.string.toolbar_title_personal_style)// ewsgdfd
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        val colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(this, R.color.white1),
            PorterDuff.Mode.SRC_ATOP
        )
        toolbar.navigationIcon?.colorFilter = colorFilter

        binding.styleDetailUserName.text = intent.getStringExtra("author")
        binding.styleDetailDescription.text = intent.getStringExtra("description")
        binding.styleDetailImages.load(intent.getStringExtra("images")){
            error(R.drawable.error_place_holder)
        }
        binding.styleDetailAvatar.load(intent.getStringExtra("avatar")){
            error(R.drawable.error_place_holder)
        }



    }
}