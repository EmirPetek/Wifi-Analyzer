package com.wifianalyzer.wifianalyzerproject.ui.fragment.aroundWifiResultsList.newVersion

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wifianalyzer.wifianalyzerproject.databinding.FragmentAroundWifiInformationDeviceResultBinding
import com.wifianalyzer.wifianalyzerproject.ui.adapter.AroundWifiResultDeviceAdapter
import com.wifianalyzer.wifianalyzerproject.viewmodel.AroundWifiInformationDeviceResultViewModel

class AroundWifiInformationResultDevice : Fragment() {



    private val viewModel: AroundWifiInformationDeviceResultViewModel by viewModels()
    private lateinit var binding: FragmentAroundWifiInformationDeviceResultBinding
    private var folderNameAsunixtimestamp : Long = 0
    private lateinit var adapter : AroundWifiResultDeviceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAroundWifiInformationDeviceResultBinding.inflate(inflater,container,false)

        binding.imageViewBackButtonAroundWifiDeviceResult.setOnClickListener { findNavController().popBackStack() }

        folderNameAsunixtimestamp = arguments?.getString("unixtimestamp")!!.toLong()

        viewModel.getRssiList(folderNameAsunixtimestamp.toString(),requireContext())
        viewModel.rssiSignalList.observe(viewLifecycleOwner, Observer { it ->
            binding.recyclerviewDeviceResult.setHasFixedSize(true)
            adapter = AroundWifiResultDeviceAdapter(requireContext(),it,folderNameAsunixtimestamp)
            binding.recyclerviewDeviceResult.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerviewDeviceResult.adapter = adapter
        })



        return binding.root
    }
}