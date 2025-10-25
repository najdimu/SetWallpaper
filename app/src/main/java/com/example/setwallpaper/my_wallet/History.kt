package com.example.setwallpaper.my_wallet

data class History (
    var idDaily: Int,
    var date: String,
    var type: String,
    var description: String,
    var amount: Double,
    var currency: String,
    var income: Boolean
)
