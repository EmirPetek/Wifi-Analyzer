package com.wifianalyzer.wifianalyzerproject.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.navigation.fragment.findNavController
import com.wifianalyzer.wifianalyzerproject.R
import com.wifianalyzer.wifianalyzerproject.databinding.FragmentHomeBinding
import java.security.SecureRandom


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)


        binding.buttonGetCurrentWifiInfo.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_currentWifiInformationFragment)  }
        binding.buttonGetScanResultData.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_aroundWifiInformation)    }
        binding.buttonGetData.setOnClickListener{ findNavController().navigate(R.id.action_homeFragment_to_aroundWifiResultsDate) }
        binding.buttonGetLocationInfo.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_currentLocationFragment) }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun checkOrSaveUser(){
        sharedPreferences = requireContext().getSharedPreferences("userInfo", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val registerState: String = sharedPreferences.getString("registerState", "0")!!
        val userKeyData: String = sharedPreferences.getString("userKey", "0")!!

        Log.e("dslkfjlsk",userKeyData)

        if (registerState == "0") {
            registerUser()
        }
    }

    fun registerUser() {
        val sharedPreferences = requireContext().getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        val userKey = generateRandomKey()
        editor.putString("userKey", userKey)
        editor.putString("registerState", "1")
        editor.commit()
    }

    fun generateRandomKey(): String {
        val characterSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val secureRandom = SecureRandom()

        // Random key will be created with 64 character
        val randomKeyBuilder = StringBuilder()
        for (i in 0..32) {
            val randomIndex = secureRandom.nextInt(characterSet.length)
            val randomChar = characterSet[randomIndex]
            randomKeyBuilder.append(randomChar)
        }
        return randomKeyBuilder.toString()
    }

}