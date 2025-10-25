package com.example.setwallpaper.my_wallet

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import org.json.JSONObject

class WalletViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefer = application.getSharedPreferences("my_wallet", Context.MODE_PRIVATE)
    private val _histories = MutableLiveData<MutableList<History>>()
    val histories: LiveData<MutableList<History>> = _histories

    init {
        val isFirst = sharedPrefer.getString("all_histories_list", "")
        isFirst?.let {
            if (it.isEmpty()) {
                _histories.value = mutableListOf()
            } else{
                val objec = JSONArray(isFirst)
                for (i in 0 until objec.length()) {
                    val json = objec.getJSONObject(i)
                    val his = History(json.getInt("idDaily"),
                        json.getString("date"),
                        json.getString("type"),
                        json.getString("description"),
                        json.getDouble("amount"),
                        json.getString("currency"),
                        json.getBoolean("income"))

                    val currentList = _histories.value ?: mutableListOf()
                    currentList.add(his)
                    _histories.value = currentList


                }

            }
        }
    }

    fun addHistory(history: History){
        val currentList = _histories.value ?: mutableListOf()
        currentList.add(history)
        // Must set value to trigger observers
        _histories.value = currentList
        println("Viewmodel")
    }

}