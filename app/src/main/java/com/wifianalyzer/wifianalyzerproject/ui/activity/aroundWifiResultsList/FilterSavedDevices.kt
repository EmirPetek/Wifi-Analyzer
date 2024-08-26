package com.wifianalyzer.wifianalyzerproject.ui.activity.aroundWifiResultsList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.wifianalyzer.wifianalyzerproject.R
import com.wifianalyzer.wifianalyzerproject.databinding.ActivityFilterSavedDevicesBinding
import com.wifianalyzer.wifianalyzerproject.ui.adapter.FilterSavedDevicesAdapter
import com.wifianalyzer.wifianalyzerproject.viewmodel.AroundWifiResultsListViewModel

class FilterSavedDevices : AppCompatActivity() {
    private lateinit var binding: ActivityFilterSavedDevicesBinding
    private val viewModel : AroundWifiResultsListViewModel by viewModels()
    private lateinit var adapter : FilterSavedDevicesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterSavedDevicesBinding.inflate(layoutInflater)

        val userkey = intent.getStringExtra("userkey")!!
        val unixtimestamp = intent.getLongExtra("unixtimestamp",0)!!

        viewModel.getDeviceList(userkey)
        viewModel.deviceList.observe(this, Observer{
            binding.recyclerViewFilterScannedDevices.setHasFixedSize(true)
            binding.recyclerViewFilterScannedDevices.layoutManager = LinearLayoutManager(this)
            adapter = FilterSavedDevicesAdapter(this,it,viewModel,this)
            binding.recyclerViewFilterScannedDevices.adapter = adapter
        })


        binding.imageViewBackButtonAroundWifiResultsListFilter.setOnClickListener{ finish() }

        binding.textViewSaveFilterScreen.setOnClickListener {
            val selectedItems = adapter.getSelectedItems()
            Log.e("return öncesi","text view tıklandı veri $selectedItems")
            val returnIntent = Intent(this,AroundWifiResultsList::class.java)
            returnIntent.putExtra("selectedList",selectedItems)
            returnIntent.putExtra("unixtimestamp",unixtimestamp.toString())
            startActivity(returnIntent)
            finish()

        }


        setContentView(binding.root)
    }
}