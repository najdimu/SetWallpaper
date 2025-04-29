package com.example.setwallpaper.style_zone.activity

import android.annotation.SuppressLint
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
import coil.load
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.ActivityPersonalStyleDetailBinding

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
        val images = intent.getStringArrayListExtra("images")
        println(images)
        binding.styleDetailImages.load(images?.get(0)){
            error(R.drawable.error_place_holder)
        }
        binding.styleDetailAvatar.load(intent.getStringExtra("avatar")){
            error(R.drawable.error_place_holder)
        }
        val themeLink = intent.getStringExtra("themeLink")
        if (!themeLink.isNullOrEmpty()){
            binding.themeLinkText.visibility = View.VISIBLE
            binding.themeLink.visibility = View.VISIBLE
            binding.themeLink.text = themeLink
        }
        binding.themeLink.setOnClickListener {
            Toast.makeText(this, "themeLink", Toast.LENGTH_SHORT).show()
        }
        val wallpaperLink = intent.getStringExtra("wallpaperLink")
        if (!wallpaperLink.isNullOrEmpty()){
            binding.wallpaperLinkText.visibility = View.VISIBLE
            binding.wallpaperLink.visibility = View.VISIBLE
            binding.wallpaperLink.text = wallpaperLink
        }
        binding.wallpaperLink.setOnClickListener {
            Toast.makeText(this, "wallpaperLink", Toast.LENGTH_SHORT).show()
        }
        val iconLink = intent.getStringExtra("iconLink")
        if (!iconLink.isNullOrEmpty()){
            binding.iconLinkText.visibility = View.VISIBLE
            binding.iconLink.visibility = View.VISIBLE
            binding.iconLink.text = iconLink
        }
        binding.iconLink.setOnClickListener {
            Toast.makeText(this, "iconLink", Toast.LENGTH_SHORT).show()
        }
        val fontLink = intent.getStringExtra("fontLink")
        if (!fontLink.isNullOrEmpty()){
            binding.fontLinkText.visibility = View.VISIBLE
            binding.fontLink.visibility = View.VISIBLE
            binding.fontLink.text = fontLink
        }
        binding.fontLink.setOnClickListener {
            Toast.makeText(this, "fontLink", Toast.LENGTH_SHORT).show()
        }
        val supportDevice = intent.getStringExtra("supportDevice")
        if (!supportDevice.isNullOrEmpty()) {
            binding.styleDetailSupport.text = "Support device: $supportDevice"
        }
    }
}