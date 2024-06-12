package com.wifianalyzer.wifianalyzerproject.ui.activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.wifianalyzer.wifianalyzerproject.databinding.ActivityAroundWifiResultsListBinding
import com.wifianalyzer.wifianalyzerproject.ui.adapter.AroundWifiResultsListAdapter
import com.wifianalyzer.wifianalyzerproject.viewmodel.AroundWifiResultsListViewModel

class AroundWifiResultsList : AppCompatActivity() {
    private val viewModel : AroundWifiResultsListViewModel by viewModels()
    private lateinit var binding: ActivityAroundWifiResultsListBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userKey: String
    private lateinit var adapter : AroundWifiResultsListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAroundWifiResultsListBinding.inflate(layoutInflater)

        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
        userKey = sharedPreferences.getString("userKey", "0")!!

        val unixtimestamp = intent.getStringExtra("unixtimestamp")!!.toLong()

        binding.progressBarAroundWifiResultsList.visibility = View.VISIBLE
        viewModel.getRssiList(unixtimestamp,userKey)
        viewModel.rssiSignalList.observe(this, Observer {
            binding.recyclerViewAroundWifiResultsList.setHasFixedSize(true)
            binding.recyclerViewAroundWifiResultsList.layoutManager = LinearLayoutManager(this)
            adapter = AroundWifiResultsListAdapter(this,it,viewModel,this)
            binding.recyclerViewAroundWifiResultsList.adapter = adapter
            binding.progressBarAroundWifiResultsList.visibility = View.GONE

        })

        binding.imageViewBackButtonAroundWifiResultsList.setOnClickListener { finish() }



        setContentView(binding.root)
    }
}