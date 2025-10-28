package com.example.setwallpaper.my_wallet

data class Wallet(
    val id: String,
    var balance: Double,
    var history: List<History>
)
