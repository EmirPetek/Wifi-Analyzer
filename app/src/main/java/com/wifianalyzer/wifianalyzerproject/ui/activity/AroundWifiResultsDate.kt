package com.wifianalyzer.wifianalyzerproject.ui.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.wifianalyzer.wifianalyzerproject.databinding.ActivityAroundWifiResultsDateBinding
import com.wifianalyzer.wifianalyzerproject.ui.adapter.AroundWifiResultsDateAdapter
import com.wifianalyzer.wifianalyzerproject.viewmodel.AroundWifiResultsDateViewModel

class AroundWifiResultsDate : AppCompatActivity() {

    private lateinit var binding:ActivityAroundWifiResultsDateBinding
    private val viewModel: AroundWifiResultsDateViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userKey: String
    private lateinit var adapter: AroundWifiResultsDateAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAroundWifiResultsDateBinding.inflate(layoutInflater)

        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
        userKey = sharedPreferences.getString("userKey", "0")!!

        binding.progressBarAroundWifiResultsDate.visibility = View.VISIBLE
        viewModel.getRssiUnixtsListData(userKey)
        viewModel.rssiSignalUnixtsList.observe(this, Observer {
            binding.recyclerViewAroundWifiResultsDate.setHasFixedSize(true)
            binding.recyclerViewAroundWifiResultsDate.layoutManager = LinearLayoutManager(this)
            adapter = AroundWifiResultsDateAdapter(this,it.reversed())
            binding.recyclerViewAroundWifiResultsDate.adapter = adapter
            binding.progressBarAroundWifiResultsDate.visibility = View.GONE
        })

        binding.imageViewBackButtonAroundWifiResultsDate.setOnClickListener { finish() }




        setContentView(binding.root)
    }
}