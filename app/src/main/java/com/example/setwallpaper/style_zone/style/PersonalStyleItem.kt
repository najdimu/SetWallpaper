package com.example.setwallpaper.style_zone.style

data class PersonalStyleItem(
    val id: Int,
    val title: String,
    val description: String,
    val mainImage: String,
    val avatar: String,
    val author: String,
    val images: List<String>,
    val themeLink: String,
    val wallpaperLink: String,
    val iconLink: String,
    val fontLink: String,
    val supportDevice: String
)
