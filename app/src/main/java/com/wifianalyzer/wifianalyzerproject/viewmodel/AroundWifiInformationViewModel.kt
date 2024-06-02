package com.wifianalyzer.wifianalyzerproject.viewmodel

import androidx.lifecycle.ViewModel
import com.wifianalyzer.wifianalyzerproject.data.RssiSignalData
import com.wifianalyzer.wifianalyzerproject.repository.RssiSignalsRepo

class AroundWifiInformationViewModel : ViewModel(){

    val rssiSignalRepo = RssiSignalsRepo()

    fun insertRssiSignal(obj: RssiSignalData, userkey: String, unixtimestamp: Long){
        rssiSignalRepo.insertRssiSignal(obj,userkey,unixtimestamp)
    }
}