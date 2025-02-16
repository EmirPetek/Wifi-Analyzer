package com.wifianalyzer.wifianalyzerproject.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wifianalyzer.wifianalyzerproject.repository.RssiSignalsRepo

class AroundWifiInformationMeasurementResultListViewModel : ViewModel() {
    private val repoRssiSignal = RssiSignalsRepo()
    var measurementResult : MutableLiveData<String> = MutableLiveData()

    fun getMeasurements(context: Context, folderName: String, txtFileName: String){
        measurementResult.value = repoRssiSignal.readFromFile(context,folderName,txtFileName)
    }


}