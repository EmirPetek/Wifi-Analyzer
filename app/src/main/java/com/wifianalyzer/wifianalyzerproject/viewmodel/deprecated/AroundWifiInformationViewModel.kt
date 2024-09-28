package com.wifianalyzer.wifianalyzerproject.viewmodel.deprecated

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wifianalyzer.wifianalyzerproject.data.RssiSignalData
import com.wifianalyzer.wifianalyzerproject.repository.RssiSignalsRepo

class AroundWifiInformationViewModel : ViewModel(){

    private val rssiSignalRepo = RssiSignalsRepo()
     var rssiList : MutableLiveData<List<RssiSignalData>> = rssiSignalRepo.rssiSignalList
     var rssiUnixtsList : MutableLiveData<List<Long>> = rssiSignalRepo.rssiSignalUnixtsList

    fun insertRssiSignal(obj: RssiSignalData, userkey: String, unixtimestamp: Long){
        rssiSignalRepo.insertRssiSignal(obj,userkey,unixtimestamp)
    }

    fun getRssiUnixtsListData(userkey: String){
        rssiSignalRepo.getRssiUnixtsListData(userkey)
       // rssiUnixtsList = rssiSignalRepo.rssiSignalUnixtsList
    }

    fun getRssiListData(unixtimestamp: Long, userkey: String){
        rssiSignalRepo.getRssiList(unixtimestamp,userkey)
      //  rssiList = rssiSignalRepo.rssiSignalList


    }
}