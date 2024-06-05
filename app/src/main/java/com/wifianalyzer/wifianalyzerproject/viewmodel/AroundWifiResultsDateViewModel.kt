package com.wifianalyzer.wifianalyzerproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wifianalyzer.wifianalyzerproject.repository.RssiSignalsRepo

class AroundWifiResultsDateViewModel : ViewModel() {

    private val repo = RssiSignalsRepo()

    var rssiSignalUnixtsList : MutableLiveData<List<Long>> = repo.rssiSignalUnixtsList

    fun getRssiUnixtsListData(userkey:String){
        repo.getRssiUnixtsListData(userkey)
    }


}