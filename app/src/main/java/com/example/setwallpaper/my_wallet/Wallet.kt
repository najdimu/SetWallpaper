package com.example.setwallpaper.my_wallet

data class Wallet(
    var id: String,
    var balance: String,
    var history: List<History>
)
