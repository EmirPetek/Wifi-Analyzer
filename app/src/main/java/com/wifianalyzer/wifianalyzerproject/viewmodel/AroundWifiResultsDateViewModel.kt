package com.wifianalyzer.wifianalyzerproject.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wifianalyzer.wifianalyzerproject.repository.RssiSignalsRepo

class AroundWifiResultsDateViewModel : ViewModel() {
    private val repo = RssiSignalsRepo()

    var rssiSignalUnixtsList : MutableLiveData<List<Long>> = MutableLiveData()

    fun getRssiUnixtsListData(context:Context){
        rssiSignalUnixtsList.value = repo.listFolders(context).map { it.toLong() }

    }
}