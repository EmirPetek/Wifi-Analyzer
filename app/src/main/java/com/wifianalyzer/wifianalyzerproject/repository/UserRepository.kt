package com.wifianalyzer.wifianalyzerproject.repository

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.wifianalyzer.wifianalyzerproject.data.User
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.LiveData

class UserRepository {

    private val _userKey = MutableLiveData<String>()
    val userKey: LiveData<String> get() = _userKey
    private var uKey = ""
    var context : Context = AppCompatActivity()

    init {
        getUserData()
        _userKey.value = uKey
    }

    private fun getUserData() {
        uKey = context
            .getSharedPreferences("userInfo", Context.MODE_PRIVATE)
            .getString("userKey", "0") ?: "0"

        _userKey.value = uKey
    }

}