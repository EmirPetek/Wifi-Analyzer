package com.wifianalyzer.wifianalyzerproject.ui.fragment.aroundWifiResultsList

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wifianalyzer.wifianalyzerproject.R
import com.wifianalyzer.wifianalyzerproject.databinding.FragmentFilterSavedDevicesBinding
import com.wifianalyzer.wifianalyzerproject.ui.adapter.FilterSavedDevicesAdapter
import com.wifianalyzer.wifianalyzerproject.viewmodel.deprecated.AroundWifiResultsListViewModel

class FilterSavedDevices : Fragment() {

    private val viewModel : AroundWifiResultsListViewModel by viewModels()
    private lateinit var binding : FragmentFilterSavedDevicesBinding
    private lateinit var adapter : FilterSavedDevicesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentFilterSavedDevicesBinding.inflate(inflater,container,false)


        val userkey = arguments?.getString("userkey")!!
        val unixtimestamp = arguments?.getLong("unixtimestamp",0)!!

        viewModel.getDeviceList(userkey)
        viewModel.deviceList.observe(viewLifecycleOwner, Observer{
            binding.recyclerViewFilterScannedDevices.setHasFixedSize(true)
            binding.recyclerViewFilterScannedDevices.layoutManager = LinearLayoutManager(requireContext())
            adapter = FilterSavedDevicesAdapter(requireContext(),it,viewModel,viewLifecycleOwner)
            binding.recyclerViewFilterScannedDevices.adapter = adapter
        })


        binding.imageViewBackButtonAroundWifiResultsListFilter.setOnClickListener{ findNavController().popBackStack() }

        binding.textViewSaveFilterScreen.setOnClickListener {
            val selectedItems = adapter.getSelectedItems() as ArrayList<String>
            Log.e("return öncesi","text view tıklandı veri $selectedItems")

            val bundle : Bundle = Bundle().apply {
                putStringArrayList("selectedList",selectedItems)
                putString("unixtimestamp",unixtimestamp.toString())
            }

            findNavController().navigate(
                R.id.action_filterSavedDevices_to_aroundWifiResultsList,
                bundle,
                NavOptions.Builder()
                    .setPopUpTo(R.id.aroundWifiResultsDate, true) // fragmentA dışındaki tüm fragment'ları geri yığından temizler
                    .build()
            )

           /* val returnIntent = Intent(this, AroundWifiResultsList::class.java)
            returnIntent.putExtra("selectedList",selectedItems)
            returnIntent.putExtra("unixtimestamp",unixtimestamp.toString())
            startActivity(returnIntent)
            finish()*/

        }

        return binding.root
    }
}