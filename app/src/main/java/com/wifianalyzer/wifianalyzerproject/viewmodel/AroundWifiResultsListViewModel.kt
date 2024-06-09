package com.wifianalyzer.wifianalyzerproject.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wifianalyzer.wifianalyzerproject.data.DevicesData
import com.wifianalyzer.wifianalyzerproject.data.RssiSignalData
import com.wifianalyzer.wifianalyzerproject.repository.DevicesRepo
import com.wifianalyzer.wifianalyzerproject.repository.RssiSignalsRepo

class AroundWifiResultsListViewModel : ViewModel() {

    private val repoRssiSignal = RssiSignalsRepo()
    var rssiSignalList : MutableLiveData<List<RssiSignalData>> = repoRssiSignal.rssiSignalList

    private val repoDevicesData = DevicesRepo()
    lateinit var deviceData : MutableLiveData<DevicesData> //= repoDevicesData.deviceData
    var deviceFoundData : MutableLiveData<String> = MutableLiveData()//= repoDevicesData.deviceData

    fun getRssiList(unixtimestamp:Long,userkey:String){
        repoRssiSignal.getRssiList(unixtimestamp,userkey)
    }

    fun insertDevice(device:DevicesData){
        repoDevicesData.insertDevice(device)
    }

    fun getIsDeviceSavedState(userkey: String, bssid:String){
        val state = repoDevicesData.checkIsDeviceSaved(userkey,bssid)
        deviceFoundData.value = state
    }

    fun getDeviceData(userkey: String, nodeKey:String){
        repoDevicesData.getDeviceData(userkey,nodeKey)
        deviceData = repoDevicesData.deviceData
        Log.e("KJFSDLKJFS", deviceData.value.toString())
    }



}