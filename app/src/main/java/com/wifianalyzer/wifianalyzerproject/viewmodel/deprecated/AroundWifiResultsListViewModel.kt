package com.wifianalyzer.wifianalyzerproject.viewmodel.deprecated

import androidx.lifecycle.LiveData
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
    var deviceFoundData : LiveData<String> = repoDevicesData.deviceSaveKey
    var deviceList : LiveData<ArrayList<DevicesData>> = repoDevicesData.deviceList


    fun getRssiList(unixtimestamp:Long,userkey:String){
        repoRssiSignal.getRssiList(unixtimestamp,userkey)
    }

    fun insertDevice(device:DevicesData){
        repoDevicesData.insertDevice(device)
    }

    fun getIsDeviceSavedState(userkey: String, bssid:String){
        repoDevicesData.checkIsDeviceSaved(userkey,bssid)
      //  Log.e("device save viewmodel nodekey -> ", state )
          //deviceFoundData.value = state
    }

    fun getDeviceData(userkey: String, nodeKey:String){
        repoDevicesData.getDeviceData(userkey,nodeKey)
        deviceData = repoDevicesData.deviceData
       // Log.e("getDevide", deviceData.value.toString())
    }

    fun getDeviceList(userkey: String){
        repoDevicesData.getDeviceList(userkey)
    }

    fun updateDeviceNickname(userkey: String,nodeKey: String,device: Map<String,String>){
        repoDevicesData.updateDeviceNickname(userkey,nodeKey, device)
    }

    fun deleteDevice(userkey: String,nodeKey: String,device: Map<String,Any>) {
        repoDevicesData.deleteDevice(userkey, nodeKey, device)
    }



}