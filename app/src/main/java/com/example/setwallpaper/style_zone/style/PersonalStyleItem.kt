package com.example.setwallpaper.style_zone.style

import java.io.Serializable


data class PersonalStyleItem(
    val id: Int,
    val title: String,
    val description: String,
    val mainImage: String,
    val avatar: String,
    val userName: String,
    val images: String,
    val linkContent: String
) : Serializable
