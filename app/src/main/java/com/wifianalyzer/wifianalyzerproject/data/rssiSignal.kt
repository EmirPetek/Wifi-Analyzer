package com.wifianalyzer.wifianalyzerproject.data

import java.io.Serializable


data class rssiSignal(
    val ssid:String,
    val bssid:String,
    val rssi:Int,
    val location:String? = null,
    val timestamp:Long,
    val userkey:String? = null) {


}