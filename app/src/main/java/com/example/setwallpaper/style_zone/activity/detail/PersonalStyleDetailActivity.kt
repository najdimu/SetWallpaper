package com.example.setwallpaper.style_zone.activity.detail

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
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
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PersonalStyleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalStyleDetailBinding
    private lateinit var viewPager: ViewPager2
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

        title = intent.getStringExtra("title") ?: resources.getString(R.string.toolbar_title_detail_style)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        val colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(this, R.color.white1),
            PorterDuff.Mode.SRC_ATOP
        )
        toolbar.navigationIcon?.colorFilter = colorFilter

        viewPager = binding.viewPagerImagesDetail

        val images = intent.getStringArrayListExtra("images")
        adapter = images?.let { DetailViewPagerAdapter(it) }!!
        viewPager.adapter = adapter

        TabLayoutMediator(binding.dotsLayout, viewPager) { tab, _ ->
            val imageView = ImageView(this).apply {
                setImageResource(R.drawable.dots_unselected)
                val size = (12 * resources.displayMetrics.density).toInt()   // 12dp
                layoutParams = ViewGroup.LayoutParams(size, size)
            }
            tab.customView = imageView
        }.attach()

        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                for (i in 0 until binding.dotsLayout.tabCount) {
                    val tab = binding.dotsLayout.getTabAt(i)
                    val imageView = tab?.customView as? ImageView
                    if (i == position) {
                        imageView?.setImageResource(R.drawable.dots_selected)
                    } else {
                        imageView?.setImageResource(R.drawable.dots_unselected)
                    }
                }
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
            binding.btnGetTheme.visibility = View.VISIBLE
        }

        val firstWallpaperLink = intent.getStringExtra("firstWallpaperLink")
        if (!firstWallpaperLink.isNullOrEmpty()){
            binding.btnSetFirstWall.visibility = View.VISIBLE
        }

        val secondWallpaperLink = intent.getStringExtra("secondWallpaperLink")
        if (!secondWallpaperLink.isNullOrEmpty()){
            binding.btnSetSecondWall.visibility = View.VISIBLE
        }

        val iconLink = intent.getStringExtra("iconLink")
        if (!iconLink.isNullOrEmpty()){
            binding.btnGetIcon.visibility = View.VISIBLE
        }

        val fontLink = intent.getStringExtra("fontLink")
        if (!fontLink.isNullOrEmpty()){
            binding.btnGetFont.visibility = View.VISIBLE
        }

        val supportDevice = intent.getStringExtra("supportDevice")
        if (!supportDevice.isNullOrEmpty()) {
            if (supportDevice == "All devices") {
                binding.styleDetailSupport.text = getString(R.string.string_all_devices)
            } else {
                binding.styleDetailSupport.text = supportDevice
            }
        }
    }

    private fun startAutoSlide() {
        autoSlideJob?.cancel()

        autoSlideJob = lifecycleScope.launch {
            while (isActive) {
                delay(4000)
                val itemCount = viewPager.adapter?.itemCount ?: 0
                if (itemCount == 0) continue

                val nextItem = if (viewPager.currentItem + 1 < itemCount ) {
                    viewPager.currentItem + 1
                } else {
                    0
                }
                viewPager.setCurrentItem(nextItem, true)
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