package com.example.setwallpaper.personalization.request

data class RequestStyleItem(
    val id: Int,
    val title: String,
    val author: String,
    val avatar: String,
    val status: Boolean,
    val date: String
)
