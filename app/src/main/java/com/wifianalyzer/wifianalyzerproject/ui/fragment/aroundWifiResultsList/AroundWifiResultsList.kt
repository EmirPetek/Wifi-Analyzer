package com.wifianalyzer.wifianalyzerproject.ui.fragment.aroundWifiResultsList

import android.content.SharedPreferences
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wifianalyzer.wifianalyzerproject.R
import com.wifianalyzer.wifianalyzerproject.data.DevicesData
import com.wifianalyzer.wifianalyzerproject.data.RssiSignalData
import com.wifianalyzer.wifianalyzerproject.databinding.FragmentAroundWifiResultsListBinding
import com.wifianalyzer.wifianalyzerproject.ui.adapter.AroundWifiResultsListAdapter
import com.wifianalyzer.wifianalyzerproject.ui.adapter.FilterSavedDevicesAdapter
import com.wifianalyzer.wifianalyzerproject.viewmodel.AroundWifiResultsListViewModel

class AroundWifiResultsList : Fragment() {


    private val viewModel: AroundWifiResultsListViewModel by viewModels()
    private lateinit var binding: FragmentAroundWifiResultsListBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userKey: String
    private lateinit var adapter: AroundWifiResultsListAdapter
    private lateinit var adapterDeviceList: FilterSavedDevicesAdapter
    private var unixtimestamp : Long = 0
    private var listData : ArrayList<RssiSignalData> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAroundWifiResultsListBinding.inflate(inflater,container,false)

        sharedPreferences = requireContext().getSharedPreferences("userInfo", MODE_PRIVATE)
        userKey = sharedPreferences.getString("userKey", "0")!!



        unixtimestamp = arguments?.getString("unixtimestamp")!!.toLong()//intent.getStringExtra("unixtimestamp")!!.toLong()

        var selectedList = arguments?.getStringArrayList("selectedList")  as? ArrayList<DevicesData>

        if (selectedList != null) {
            viewModel.getRssiList(unixtimestamp, userKey)
            viewModel.rssiSignalList.observe(viewLifecycleOwner, Observer {
                binding.recyclerViewAroundWifiResultsList.setHasFixedSize(true)
                binding.recyclerViewAroundWifiResultsList.layoutManager = LinearLayoutManager(requireContext())

                for (s in selectedList){
                    for (l in it){
                        if (s.bssid == l.bssid){
                            listData.add(l)
                        }
                    }
                }

                adapter = AroundWifiResultsListAdapter(requireContext(), listData, viewModel, this)
                binding.recyclerViewAroundWifiResultsList.adapter = adapter
                binding.progressBarAroundWifiResultsList.visibility = View.GONE

            })


            binding.progressBarAroundWifiResultsList.visibility = View.VISIBLE
        }else {
            viewModel.getRssiList(unixtimestamp, userKey)
            viewModel.rssiSignalList.observe(viewLifecycleOwner, Observer {
                binding.recyclerViewAroundWifiResultsList.setHasFixedSize(true)
                binding.recyclerViewAroundWifiResultsList.layoutManager = LinearLayoutManager(requireContext())
                adapter = AroundWifiResultsListAdapter(requireContext(), it, viewModel, this)
                binding.recyclerViewAroundWifiResultsList.adapter = adapter
                binding.progressBarAroundWifiResultsList.visibility = View.GONE

            })
        }

        binding.imageViewBackButtonAroundWifiResultsList.setOnClickListener { findNavController().popBackStack() }

        binding.imageButtonFilterDataAroundWifiResults.setOnClickListener {

            val bundle = Bundle().apply {
                putString("userkey",userKey)
                putLong("unixtimestamp",unixtimestamp)
            }

            findNavController().navigate(R.id.action_aroundWifiResultsList_to_filterSavedDevices,bundle)

           /* val intent = Intent(this, FilterSavedDevices::class.java)
            intent.putExtra("userkey",userKey)
            intent.putExtra("unixtimestamp",unixtimestamp)
            startActivity(intent)
            finish()*/
            // startActivityForResult(intent, 100)
        }

        return binding.root
    }
}