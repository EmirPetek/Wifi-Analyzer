package com.wifianalyzer.wifianalyzerproject.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import com.wifianalyzer.wifianalyzerproject.data.RssiSignal
import com.wifianalyzer.wifianalyzerproject.repository.RssiSignalsRepo

class AroundWifiInformationViewModel : ViewModel(){

    val rssiSignalRepo = RssiSignalsRepo()

    fun insertRssiSignal(obj: RssiSignal, timestamp:Long){
        rssiSignalRepo.insertRssiSignal(obj,timestamp)
    }
}