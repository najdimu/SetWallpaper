package com.example.setwallpaper.my_wallet

data class History (
    val idDaily: Int,
    val weekNum: Int,
    val startWeek: String,
    val endWeek: String,
    val monthName: String,
    val date: String,
    val type: String,
    val icon: Int,
    val description: String,
    val iconPayment: Int,
    val paymentMethod: String,
    val amount: Double,
    val currency: String,
    val income: Boolean
)
