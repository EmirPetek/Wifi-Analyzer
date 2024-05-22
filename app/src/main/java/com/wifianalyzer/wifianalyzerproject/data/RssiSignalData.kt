package com.wifianalyzer.wifianalyzerproject.data


data class RssiSignalData(
    val ssid:String,
    val bssid:String,
    val rssi:Int,
    val location:String? = null,
    val timestamp:Long,
    val userkey:String? = null) {


}