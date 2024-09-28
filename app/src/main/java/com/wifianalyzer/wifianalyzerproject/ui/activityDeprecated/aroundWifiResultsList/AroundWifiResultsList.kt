package com.wifianalyzer.wifianalyzerproject.ui.activityDeprecated.aroundWifiResultsList

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.wifianalyzer.wifianalyzerproject.data.DevicesData
import com.wifianalyzer.wifianalyzerproject.data.RssiSignalData
import com.wifianalyzer.wifianalyzerproject.databinding.ActivityAroundWifiResultsListBinding
import com.wifianalyzer.wifianalyzerproject.ui.adapter.AroundWifiResultsListAdapter
import com.wifianalyzer.wifianalyzerproject.ui.adapter.FilterSavedDevicesAdapter
import com.wifianalyzer.wifianalyzerproject.viewmodel.deprecated.AroundWifiResultsListViewModel

class AroundWifiResultsList : AppCompatActivity() {
    private val viewModel: AroundWifiResultsListViewModel by viewModels()
    private lateinit var binding: ActivityAroundWifiResultsListBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userKey: String
    private lateinit var adapter: AroundWifiResultsListAdapter
    private lateinit var adapterDeviceList: FilterSavedDevicesAdapter
    private var unixtimestamp : Long = 0
    private var listData : ArrayList<RssiSignalData> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAroundWifiResultsListBinding.inflate(layoutInflater)

        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
        userKey = sharedPreferences.getString("userKey", "0")!!

        unixtimestamp = intent.getStringExtra("unixtimestamp")!!.toLong()

        var selectedList = intent.getSerializableExtra("selectedList")  as? ArrayList<DevicesData>

        /*if (selectedList != null) {
            viewModel.getRssiList(unixtimestamp, userKey)
            viewModel.rssiSignalList.observe(this, Observer {
                binding.recyclerViewAroundWifiResultsList.setHasFixedSize(true)
                binding.recyclerViewAroundWifiResultsList.layoutManager = LinearLayoutManager(this)

                for (s in selectedList){
                    for (l in it){
                        if (s.bssid == l.bssid){
                            listData.add(l)
                        }
                    }
                }

                adapter = AroundWifiResultsListAdapter(this, listData, viewModel, this)
                binding.recyclerViewAroundWifiResultsList.adapter = adapter
                binding.progressBarAroundWifiResultsList.visibility = View.GONE

            })


                binding.progressBarAroundWifiResultsList.visibility = View.VISIBLE
        }else {
            viewModel.getRssiList(unixtimestamp, userKey)
            viewModel.rssiSignalList.observe(this, Observer {
                binding.recyclerViewAroundWifiResultsList.setHasFixedSize(true)
                binding.recyclerViewAroundWifiResultsList.layoutManager = LinearLayoutManager(this)
                adapter = AroundWifiResultsListAdapter(this, it, viewModel, this)
                binding.recyclerViewAroundWifiResultsList.adapter = adapter
                binding.progressBarAroundWifiResultsList.visibility = View.GONE

            })
        }*/

        binding.imageViewBackButtonAroundWifiResultsList.setOnClickListener { finish() }

        binding.imageButtonFilterDataAroundWifiResults.setOnClickListener {
            val intent = Intent(this, FilterSavedDevices::class.java)
            intent.putExtra("userkey",userKey)
            intent.putExtra("unixtimestamp",unixtimestamp)
            startActivity(intent)
            finish()
           // startActivityForResult(intent, 100)
        }

        setContentView(binding.root)
    }
/*
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val result = data?.getSerializableExtra("selectedList")
            var newList: List<RssiSignalData>
            viewModel.getRssiList(unixtimestamp, userKey)
            viewModel.rssiSignalList.observe(this, Observer {
                binding.recyclerViewAroundWifiResultsList.setHasFixedSize(true)
                binding.recyclerViewAroundWifiResultsList.layoutManager = LinearLayoutManager(this)
                for (i in it) {

                }
                adapter = AroundWifiResultsListAdapter(this, it, viewModel, this)
                binding.recyclerViewAroundWifiResultsList.adapter = adapter
                binding.progressBarAroundWifiResultsList.visibility = View.GONE

            })

        }


    }*/
}