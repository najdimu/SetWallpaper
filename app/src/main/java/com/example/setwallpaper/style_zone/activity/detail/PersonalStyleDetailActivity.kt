package com.example.setwallpaper.style_zone.activity.detail

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import coil.load
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.ActivityPersonalStyleDetailBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PersonalStyleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalStyleDetailBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var dotsLayout: LinearLayout
    private lateinit var adapter: DetailViewPagerAdapter

    private var autoSlideJob: Job? = null

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

        viewPager = binding.viewPagerImagesDetail
        dotsLayout = binding.dotsLayout

        val images = intent.getStringArrayListExtra("images")
        adapter = images?.let { DetailViewPagerAdapter(it) }!!
        viewPager.adapter = adapter

        setupDots(images.size)
        println(images.size)
        setupDots(0)

        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                selectDot(position)
            }
        })
        startAutoSlide()

        binding.styleDetailUserName.text = intent.getStringExtra("author")
        binding.styleDetailDescription.text = intent.getStringExtra("description")

        binding.styleDetailAvatar.load(intent.getStringExtra("avatar")){
            error(R.drawable.error_place_holder)
        }
        val themeLink = intent.getStringExtra("themeLink")
        if (!themeLink.isNullOrEmpty()){
            binding.themeLinkText.visibility = View.VISIBLE
            binding.themeLink.visibility = View.VISIBLE
            binding.themeLink.text = themeLink
        }

        val wallpaperLink = intent.getStringExtra("wallpaperLink")
        if (!wallpaperLink.isNullOrEmpty()){
            binding.wallpaperLinkText.visibility = View.VISIBLE
            binding.wallpaperLink.visibility = View.VISIBLE
            binding.wallpaperLink.text = wallpaperLink
        }

        val iconLink = intent.getStringExtra("iconLink")
        if (!iconLink.isNullOrEmpty()){
            binding.iconLinkText.visibility = View.VISIBLE
            binding.iconLink.visibility = View.VISIBLE
            binding.iconLink.text = iconLink
        }

        val fontLink = intent.getStringExtra("fontLink")
        if (!fontLink.isNullOrEmpty()){
            binding.fontLinkText.visibility = View.VISIBLE
            binding.fontLink.visibility = View.VISIBLE
            binding.fontLink.text = fontLink
        }

        val supportDevice = intent.getStringExtra("supportDevice")
        if (!supportDevice.isNullOrEmpty()) {
            binding.styleDetailSupport.text = "Support device: $supportDevice"
        }
    }

    private fun setupDots(count: Int){
        dotsLayout.removeAllViews()
        for (i in 0 until count){
            val dot = ImageView(this).apply {
                setImageResource(R.drawable.dots_unselected)
                val params = LinearLayout.LayoutParams(20, 20).apply {
                    marginStart = 8
                    marginEnd = 8
                }
                layoutParams = params
            }
            dotsLayout.addView(dot)
        }
    }
    private fun selectDot(position: Int) {
        for (i in 0 until dotsLayout.childCount) {
            val imageView = dotsLayout.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageResource(R.drawable.dots_selected)
            } else {
                imageView.setImageResource(R.drawable.dots_unselected)
            }
        }
    }
    private fun startAutoSlide() {
        autoSlideJob = lifecycleScope.launch {
            while (isActive) {
                delay(5000)
                val next = viewPager.currentItem + 1
                viewPager.setCurrentItem(next, true)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        autoSlideJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        startAutoSlide()
    }
}