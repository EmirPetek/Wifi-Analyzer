package com.wifianalyzer.wifianalyzerproject.repository.`interface`

import com.wifianalyzer.wifianalyzerproject.data.RssiSignalData

interface rssiSignalRepoFunctions{
    fun insertRssiSignal(obj: RssiSignalData, userkey: String, unixtimestamp: Long){}
    fun getRssiUnixtsListData(userkey: String){}
    fun getRssiList(unixtimestamp: List<Long>, userkey: String){} // to run, needed getRssiUnixtsListData result
}