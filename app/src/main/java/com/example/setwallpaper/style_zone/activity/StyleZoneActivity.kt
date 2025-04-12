package com.example.setwallpaper.style_zone.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.setwallpaper.R
import com.example.setwallpaper.databinding.ActivityStyleZoneBinding
import com.example.setwallpaper.style_zone.StyleZoneViewPagerAdapter
import com.google.android.material.tabs.TabLayout

class StyleZoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStyleZoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStyleZoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.style_zone_activity_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tabLayout = binding.tabLayoutStyleZone

        val adapter = StyleZoneViewPagerAdapter(this)
        binding.viewPagerStyleZone.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPagerStyleZone.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        binding.viewPagerStyleZone.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

    }
}