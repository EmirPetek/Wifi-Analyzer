package com.wifianalyzer.wifianalyzerproject.ui.fragment

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
import com.wifianalyzer.wifianalyzerproject.databinding.FragmentAroundWifiResultsDateBinding
import com.wifianalyzer.wifianalyzerproject.ui.adapter.AroundWifiResultsDateAdapter
import com.wifianalyzer.wifianalyzerproject.viewmodel.AroundWifiResultsDateViewModel

class AroundWifiResultsDate : Fragment() {

    private val viewModel: AroundWifiResultsDateViewModel by viewModels()
    private lateinit var binding: FragmentAroundWifiResultsDateBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userKey: String
    private lateinit var adapter: AroundWifiResultsDateAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAroundWifiResultsDateBinding.inflate(inflater,container,false)

        sharedPreferences = requireContext().getSharedPreferences("userInfo", MODE_PRIVATE)
        userKey = sharedPreferences.getString("userKey", "0")!!

        binding.progressBarAroundWifiResultsDate.visibility = View.VISIBLE
        viewModel.getRssiUnixtsListData(requireContext())
        viewModel.rssiSignalUnixtsList.observe(viewLifecycleOwner, Observer {
            binding.recyclerViewAroundWifiResultsDate.setHasFixedSize(true)
            binding.recyclerViewAroundWifiResultsDate.layoutManager = LinearLayoutManager(requireContext())
            adapter = AroundWifiResultsDateAdapter(requireContext(),it.reversed())
            binding.recyclerViewAroundWifiResultsDate.adapter = adapter
            binding.progressBarAroundWifiResultsDate.visibility = View.GONE
        })

        binding.imageViewBackButtonAroundWifiResultsDate.setOnClickListener { findNavController().popBackStack() }


        return binding.root
    }
}