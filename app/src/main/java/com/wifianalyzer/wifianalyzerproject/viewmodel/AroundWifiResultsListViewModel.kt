package com.wifianalyzer.wifianalyzerproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wifianalyzer.wifianalyzerproject.data.RssiSignalData
import com.wifianalyzer.wifianalyzerproject.repository.RssiSignalsRepo

class AroundWifiResultsListViewModel : ViewModel() {

    private val repo = RssiSignalsRepo()
    var rssiSignalList : MutableLiveData<List<RssiSignalData>> = repo.rssiSignalList

    fun getRssiList(unixtimestamp:Long,userkey:String){
        repo.getRssiList(unixtimestamp,userkey)
    }



}