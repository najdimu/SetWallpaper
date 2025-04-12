package com.example.setwallpaper.style_zone

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.setwallpaper.style_zone.fragment.InspireMeFragment
import com.example.setwallpaper.style_zone.fragment.OverviewThemesFragment

class StyleZoneViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf(InspireMeFragment(), OverviewThemesFragment())
    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}