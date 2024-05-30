package com.wifianalyzer.wifianalyzerproject.data

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class RssiSignalData(
    val ssid:String? = null,
    val bssid:String? = null,
    val rssi:Int? = null,
    val location:String? = null,
    val timestamp:Long? = null,
    val userkey:String? = null) : Serializable {


}