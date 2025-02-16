package com.wifianalyzer.wifianalyzerproject.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wifianalyzer.wifianalyzerproject.data.DevicesData
import com.wifianalyzer.wifianalyzerproject.data.RssiSignalData
import com.wifianalyzer.wifianalyzerproject.repository.DevicesRepo
import com.wifianalyzer.wifianalyzerproject.repository.RssiSignalsRepo

class AroundWifiInformationDeviceResultViewModel : ViewModel() {
    private val repoRssiSignal = RssiSignalsRepo()
    var rssiSignalList : MutableLiveData<List<String>> = MutableLiveData()


    fun getRssiList(folderName:String,context: Context){
        rssiSignalList.value = repoRssiSignal.listTxtFiles(folderName, context)
        Log.e("listttt", repoRssiSignal.listTxtFiles(folderName, context).toString())
    }
}