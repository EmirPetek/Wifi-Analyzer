package com.wifianalyzer.wifianalyzerproject.ui.fragment.aroundWifiResultsList.newVersion

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wifianalyzer.wifianalyzerproject.R
import com.wifianalyzer.wifianalyzerproject.data.MeasurementParameters
import com.wifianalyzer.wifianalyzerproject.databinding.FragmentAroundWifiInformationMeasurementResultListBinding
import com.wifianalyzer.wifianalyzerproject.ui.adapter.AroundWifiInformationMeasurementResultListAdapter
import com.wifianalyzer.wifianalyzerproject.ui.adapter.AroundWifiResultDeviceAdapter
import com.wifianalyzer.wifianalyzerproject.viewmodel.AroundWifiInformationMeasurementResultListViewModel

class AroundWifiInformationMeasurementResultList : Fragment() {

    private val viewModel: AroundWifiInformationMeasurementResultListViewModel by viewModels()
    private lateinit var binding: FragmentAroundWifiInformationMeasurementResultListBinding
    private lateinit var adapter: AroundWifiInformationMeasurementResultListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAroundWifiInformationMeasurementResultListBinding.inflate(inflater,container,false)

        binding.imageViewBackButtonAroundWifiMeasurementResult.setOnClickListener { findNavController().popBackStack() }

        val folderName = arguments?.getString("folderName")!!
        val txtFileName = arguments?.getString("txtFileName")!!

        Log.e("folderAndTxtFileName: ","folder: $folderName && txtFileName: $txtFileName")

        viewModel.getMeasurements(requireContext(),folderName,txtFileName)
        viewModel.measurementResult.observe(viewLifecycleOwner, Observer { it ->
            Log.e("AroundWifiInformationMeasurementResultList",it)
            binding.recyclerviewAroundWifiInformationMeasurementResult.setHasFixedSize(true)
            adapter = AroundWifiInformationMeasurementResultListAdapter(parseMeasurementDataFromTxt(it))
            binding.recyclerviewAroundWifiInformationMeasurementResult.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerviewAroundWifiInformationMeasurementResult.adapter = adapter
        })



        return binding.root
    }

    fun parseMeasurementDataFromTxt(data: String): ArrayList<MeasurementParameters> {
        val measurementList = ArrayList<MeasurementParameters>()

        // Satır bazında ayır
        val lines = data.lines()

        for (line in lines) {
            // Boş satırları atla
            if (line.isBlank()) continue

            // Satırı virgüle göre parçala
            val values = line.split(",").filter { it.isNotBlank() }

                try {
                    val measurement = MeasurementParameters(
                        rssi = values[0],
                        accelerometerX = values[1],
                        accelerometerY = values[2],
                        accelerometerZ = values[3],
                        gyroscopeX = values[4],
                        gyroscopeY = values[5],
                        gyroscopeZ = values[6],
                        deviceLocationX = values[7],
                        deviceLocationY = values[8],
                        deviceLocationZ = values[9]
                    )
                    measurementList.add(measurement)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

        }

        return measurementList
    }

}